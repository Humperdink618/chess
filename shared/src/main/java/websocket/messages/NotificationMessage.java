package websocket.messages;

public class NotificationMessage extends ServerMessage {

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public NotificationMessage(ServerMessageType type) {
        super(type);
    }
}
