package ir.saga.rabbitmq;

import com.rabbitmq.client.*;
import ir.saga.error.SubscribChannelException;
import ir.saga.message.Message;
import ir.saga.message.MessageImpl;
import ir.saga.message.consumer.MessageConsumer;
import ir.saga.message.consumer.MessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.SerializationUtils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeoutException;

public class MessageConsumerRabbitImpl extends MessageConsumer implements Consumer {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private Map<String, RabbitConsumer> rabbitConsumers = new HashMap<>();
    private final Map<String, List<MessageHandler>> subscriptions;
    private final ConnectionFactory connectionFactory;

    public MessageConsumerRabbitImpl(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
        subscriptions = new HashMap<>();
    }

    @Override
    public void subscribe(String subscriberId, Set<String> channelNames, MessageHandler handler) {
        try {
            for (String channelName : channelNames) {

                if (!rabbitConsumers.containsKey(channelName)) {
                    RabbitConsumer consumer = new RabbitConsumer(connectionFactory, channelName, this);
                    Thread thread = new Thread(consumer);
                    thread.start();
                    rabbitConsumers.put(channelName, consumer);
                }

                List<MessageHandler> handlers = subscriptions.get(channelName);
                if (handlers == null) {
                    handlers = new ArrayList<>();
                    subscriptions.put(channelName, handlers);
                }
                handlers.add(handler);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new SubscribChannelException(ex.getMessage());
        }


    }

    @Override
    public void close() throws IOException, TimeoutException {
        subscriptions.clear();
        for (Map.Entry<String, RabbitConsumer> entry : rabbitConsumers.entrySet()) {
            RabbitConsumer rabbitConsumer = entry.getValue();
            rabbitConsumer.close();
        }
        rabbitConsumers.clear();
    }


    private void onReceive(String channel, Message message) {
        try {
            if (preInterceptors != null) {
                preInterceptors.stream().forEach(messagePostInterceptor -> {
                    messagePostInterceptor.doInterceptor(message);
                });
            }
            List<MessageHandler> handlers = subscriptions.getOrDefault(channel, Collections.emptyList());
            for (MessageHandler handler : handlers) {
                try {
                    handler.accept(channel, message);
                } catch (Throwable t) {
                    logger.error("message handler " + channel, t);
                }
            }
        } finally {

            if (postInterceptor != null) {
                postInterceptor.stream().forEach(messagePostInterceptor -> {
                    messagePostInterceptor.doInterceptor(message);
                });
            }
        }
    }

    @Override
    public void handleConsumeOk(String consumerTag) {

    }

    @Override
    public void handleCancelOk(String consumerTag) {

    }

    @Override
    public void handleCancel(String consumerTag) throws IOException {

    }

    @Override
    public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {
    }

    @Override
    public void handleRecoverOk(String consumerTag) {

    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        this.onReceive(envelope.getExchange(), (MessageImpl) SerializationUtils.deserialize(body));
    }
}
