package subscribe.response_messages;

public class ResponseMessage {
    private String message;

    public ResponseMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "ResponseMessage{" +
                "message='" + message + '\'' +
                '}';
    }
}
