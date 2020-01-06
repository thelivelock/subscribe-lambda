package subscribe.response_messages;

public class InvalidUserSubscription extends ResponseMessage {
    public InvalidUserSubscription() {
        super("Invalid payload, don't go down to AWS to waste money and free tier quota.");
    }
}
