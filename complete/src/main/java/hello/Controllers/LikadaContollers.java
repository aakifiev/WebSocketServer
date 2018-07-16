package hello.Controllers;

import hello.model.CarPark;
import hello.model.LikadaMessageProtocol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.WebSocketSession;

import java.util.Date;
import java.util.Map;

@Controller
@EnableScheduling
public class LikadaContollers {

    @Autowired
    private CarPark carPark;

    @Scheduled(fixedDelay = 30000)
    public void checkConnection() {
        Date currentDate = new Date();
        Map<String, WebSocketSession> sessions = carPark.getSessions();
        carPark.getLastPings().forEach((deviceId, date) -> {
            if (currentDate.getTime() - date.getTime() > 30000L) {
                sessions.put(deviceId, null);
            }
        });
    }

    //не удалять
    /*@MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Map<String, Boolean> greeting(LikadaMessageProtocol message) throws Exception {
        //public Greeting greeting(@PathVariable("userName") String name) throws Exception {
        System.out.println("greeting");
        //Thread.sleep(1000); // simulated delay
        return carPark.getSessions().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue() == null));
    }*/

    @MessageMapping("/test")
    @SendTo("/topic/greetings")
    public LikadaMessageProtocol testMessage(LikadaMessageProtocol message) throws Exception {
        System.out.println("greeting");
        return message;
    }

}
