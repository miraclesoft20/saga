package ir.saga.message;

public interface BrokerReceiver {
    void onReceive(String channel, Message message);
}
