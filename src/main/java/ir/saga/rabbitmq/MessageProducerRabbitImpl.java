package ir.saga.rabbitmq;

import com.rabbitmq.client.*;
import ir.saga.command.SagaCommandHeaders;
import ir.saga.error.FailedSendExeption;
import ir.saga.events.DomainEventPublisher;
import ir.saga.events.MessageTimeoutEvent;
import ir.saga.message.Message;
import ir.saga.message.MessageImpl;
import ir.saga.message.interceptor.MessagePreInterceptor;
import ir.saga.message.producer.MessageProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.util.SerializationUtils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeoutException;

public class MessageProducerRabbitImpl implements MessageProducer, Consumer {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Value("${saga.rabbitmq.message.timeout}")
    private String messageTimeout;
    private Map<String, Connector> connectors = new HashMap<>();
    private Set<String> sagaTypes = new HashSet<>();
    private final ConnectionFactory connectionFactory;
    private final DomainEventPublisher domainEventPublisher;

    @Lazy
    @Autowired(required = false)
    private MessagePreInterceptor messagePreInterceptor;

    public MessageProducerRabbitImpl(ConnectionFactory connectionFactory, DomainEventPublisher domainEventPublisher) {
        this.connectionFactory = connectionFactory;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    public void send(String destination, Message message) {
        try {
            this.sendWithExpiration(message, destination, getConnector(destination, false).getChannel());
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new FailedSendExeption();
        }
    }

    @Override
    public void sendWithTimeoutListener(String destination, Message message) {
        try {
            sagaTypes.add(message.getRequiredHeader(SagaCommandHeaders.SAGA_TYPE));
            this.sendWithExpiration(message, destination, getConnector(destination, true).getChannel());
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new FailedSendExeption();
        }
    }

    private Connector getConnector(String destination, boolean withTimeout) throws IOException, TimeoutException {
        if (!connectors.containsKey(destination)) {
            connectors.put(destination, new Connector(connectionFactory, destination));
            Connector connector = connectors.get(destination);
            if (withTimeout) {
                connector.getChannel().basicConsume(connector.getTimeoutQuename(), false, this);
            }

        }

        return connectors.get(destination);
    }

    private void sendWithExpiration(Message message, String destination, Channel channel) throws IOException {
        channel.basicPublish(destination, "", new AMQP.BasicProperties.
                        Builder().deliveryMode(2)
                        .expiration(messageTimeout)
                        .build()
                , SerializationUtils.serialize(message));
    }

    private void send(Message message, String destination, Channel channel) throws IOException {
        channel.basicPublish(destination, "", null
                , SerializationUtils.serialize(message));
    }

    @Override
    public void close() throws IOException, TimeoutException {
        for (Map.Entry<String, Connector> entry : connectors.entrySet()) {
            Connector connector = entry.getValue();
            connector.close();
        }
        connectors.clear();
    }

    @Override
    public void handleConsumeOk(String consumerTag) {
        logger.info("handleConsumeOk resive {}", consumerTag);
    }

    @Override
    public void handleCancelOk(String consumerTag) {
        logger.info("handleCancelOk resive {}", consumerTag);
    }

    @Override
    public void handleCancel(String consumerTag) throws IOException {
        logger.info("handleCancel resive {}", consumerTag);
    }

    @Override
    public void handleShutdownSignal(String consumerTag, ShutdownSignalException e) {
        logger.info("handleShutdownSignal resive {}", consumerTag);
    }

    @Override
    public void handleRecoverOk(String consumerTag) {
        logger.info("handleRecoverOk resive {}", consumerTag);
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope,
                               AMQP.BasicProperties basicProperties,
                               byte[] body) throws IOException {
        logger.info("timeout in {}", envelope.getExchange());
        Connector connector = connectors.get(envelope.getExchange().replace(Connector.TIMEOUT_SUFFIX, ""));
        Message message = (MessageImpl) SerializationUtils.deserialize(body);
        if (sagaTypes.contains(message.getRequiredHeader(SagaCommandHeaders.SAGA_TYPE))) {
            logger.debug("timeout event for {}", envelope.getExchange());
            connector.getChannel().basicAck(envelope.getDeliveryTag(), false);

            List<MessageTimeoutEvent> timeoutEvents = new ArrayList<>();
            timeoutEvents.add(new MessageTimeoutEvent(message));

            if (messagePreInterceptor != null) {
                messagePreInterceptor.doInterceptor(message);
            }
            domainEventPublisher.publish(message.getRequiredHeader(SagaCommandHeaders.SAGA_TYPE), null, timeoutEvents);
        } else {
            connector.getChannel().basicNack(envelope.getDeliveryTag(), false, true);
        }
    }
}
