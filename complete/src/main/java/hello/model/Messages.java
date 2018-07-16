package hello.model;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class Messages {

    private final Map<String, MessageStatusEnum> messages = new ConcurrentHashMap<>();

    public Map<String, MessageStatusEnum> getMessages() {
        return messages;
    }
}
