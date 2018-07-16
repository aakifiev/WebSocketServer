package hello.model;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class CarPark {
    private Map<String, WebSocketSession> sessions = new HashMap<>();
    private Map<String, Date> lastPings = new HashMap<>();

    public Map<String, WebSocketSession> getSessions() {
        return sessions;
    }

    public void setSessions(Map<String, WebSocketSession> sessions) {
        this.sessions = sessions;
    }

    public Map<String, Date> getLastPings() {
        return lastPings;
    }

    public void setLastPings(Map<String, Date> lastPings) {
        this.lastPings = lastPings;
    }
}
