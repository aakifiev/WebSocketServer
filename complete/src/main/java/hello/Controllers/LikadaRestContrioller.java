package hello.Controllers;

import com.google.gson.Gson;
import hello.model.CarPark;
import hello.model.LikadaMessageProtocol;
import hello.model.MessageStatusEnum;
import hello.model.Messages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@EnableScheduling
public class LikadaRestContrioller {

    @Autowired
    private CarPark carPark;
    @Autowired
    private Messages messages;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @RequestMapping(value = "/sessions")
    public Set<String> getSessions() {
        System.out.println("getSessions");
        return carPark.getSessions().keySet();
    }

    @RequestMapping(value = "/availableSessions")
    public Map<String, Boolean> getAvailableSessions() {
        System.out.println("get available sessions");
        return carPark.getSessions().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue() != null));
    }

    @RequestMapping(value = "/send/{deviceId}")
    public MessageStatusEnum sendMessage(@PathVariable String deviceId) {
        System.out.println("sendMessage");
        String messageId = String.valueOf(System.currentTimeMillis());
        try {
            WebSocketSession webSocketSession = carPark.getSessions().get(deviceId);
            if (webSocketSession == null) {
                return MessageStatusEnum.NOT_SEND;
            } else {
                Gson gson = new Gson();
                LikadaMessageProtocol likadaMessageProtocol = new LikadaMessageProtocol();

                likadaMessageProtocol.setMessageId(messageId);
                likadaMessageProtocol.setMessage("Проверка");
                String json = gson.toJson(likadaMessageProtocol);
                webSocketSession.sendMessage(new TextMessage(json));
                messages.getMessages().put(messageId, MessageStatusEnum.SEND);
                Thread.sleep(10000L);
            }
        } catch (IOException e) {
            return MessageStatusEnum.NOT_SEND;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return messages.getMessages().get(messageId);
    }

    @RequestMapping(value = "/check/{deviceId}")
    public boolean checkConnect(@PathVariable String deviceId) {
        return carPark.getSessions().get(deviceId) != null;
    }

    @RequestMapping(value = "/sendAll")
    public void sendBroadcast() throws Exception {
        LikadaMessageProtocol likadaMessageProtocol = new LikadaMessageProtocol();
        likadaMessageProtocol.setMessage("Broadcast test");
        simpMessagingTemplate.convertAndSend("/topic/greetings", likadaMessageProtocol);
    }

}
