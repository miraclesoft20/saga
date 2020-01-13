package ir.saga.events;

import ir.saga.message.Message;

public class MessageTimeoutEvent implements DomainEvent {
    private Message message;

    public MessageTimeoutEvent(Message message) {
        this.message = message;
    }

    public MessageTimeoutEvent() {
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
