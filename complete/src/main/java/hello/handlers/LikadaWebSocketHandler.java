package hello.handlers;

import com.google.gson.Gson;
import hello.model.CarPark;
import hello.model.LikadaMessageProtocol;
import hello.model.MessageStatusEnum;
import hello.model.Messages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public class LikadaWebSocketHandler extends TextWebSocketHandler implements WebSocketHandler {

    @Autowired
    private CarPark carPark;
    @Autowired
    private Messages messages;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        Gson gson = new Gson();
        LikadaMessageProtocol likadaMessageProtocol = gson.fromJson(message.getPayload(), LikadaMessageProtocol.class);
        messages.getMessages().put(likadaMessageProtocol.getMessageId(), MessageStatusEnum.DELIVERED);
        updateSessions(session);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // The WebSocket has been opened
        // I might save this session object so that I can send messages to it outside of this method

        // Let's send the first message
        List<String> deviceIdList = session.getHandshakeHeaders().get("deviceId");


        if (deviceIdList != null && !deviceIdList.isEmpty()) {
            String deviceId = deviceIdList.get(0);
            session.sendMessage(new TextMessage("You are now connected as " + deviceId));
            carPark.getSessions().put(deviceId, session);
            carPark.getLastPings().put(deviceId, new Date());
        } else {
            session.sendMessage(new TextMessage("You are now connected as unknown"));
        }

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Optional.ofNullable(session.getHandshakeHeaders().get("deviceId"))
                .map(httpHeaders -> httpHeaders.get(0))
                .ifPresent(s -> {
                    carPark.getSessions().remove(s);
                    carPark.getLastPings().remove(s);
                });

    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        updateSessions(session);
    }

    private void updateSessions(WebSocketSession session) {
        Optional.ofNullable(session.getHandshakeHeaders().get("deviceId"))
                .map(httpHeaders -> httpHeaders.get(0))
                .ifPresent(s -> {
                    carPark.getLastPings().put(s, new Date());
                    carPark.getSessions().put(s, session);
                });
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        super.handleTransportError(session, exception);
    }

    @Override
    protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
        super.handlePongMessage(session, message);
    }

    @Override
    public boolean supportsPartialMessages() {
        return super.supportsPartialMessages();
    }
}
