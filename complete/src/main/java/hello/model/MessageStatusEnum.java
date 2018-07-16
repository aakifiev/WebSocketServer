package hello.model;

public enum MessageStatusEnum {

    DELIVERED("Доставлен"),
    NOT_DELIVERED("Не доставлен"),
    NOT_SEND("Не отправлен"),
    SEND("Отправлен");

    private final String name;

    MessageStatusEnum(String name) {
        this.name = name;
    }
}
