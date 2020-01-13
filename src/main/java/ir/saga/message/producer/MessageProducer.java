package ir.saga.message.producer;

import ir.saga.message.Message;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public interface MessageProducer {
    /**
     * Send a message
     * @param destination the destination channel
     * @param message the message to doSend
     * @see Message
     */
    void send(String destination, Message message);
    void sendWithTimeoutListener(String destination, Message message);
    void close() throws IOException, TimeoutException;
}
