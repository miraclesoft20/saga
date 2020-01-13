package ir.saga.rabbitmq;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import ir.saga.error.ConsumeChannelException;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitConsumer extends Connector implements Runnable {
    private final Consumer consumer;

    public RabbitConsumer(ConnectionFactory connectionFactory, String queueName, Consumer consumer) throws IOException, TimeoutException {
        super(connectionFactory, queueName);
        this.consumer = consumer;
    }

    /*public void basicAck(long deliveryTag) throws IOException {
        this.getChannel().basicAck(deliveryTag,false);
    }*/

    @Override
    public void run() {
        try {
            getChannel().basicConsume(getQueueName(), true, consumer);
        } catch (Exception ex) {
            throw new ConsumeChannelException(getQueueName());
        }
    }
}
