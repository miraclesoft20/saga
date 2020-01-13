package ir.saga.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class Connector {
    public static final String TIMEOUT_SUFFIX = "-timeout";
    private Channel channel;
    private Connection connection;
    private String queueName;


    public Connector(ConnectionFactory connectionFactory, String queueName) throws IOException, TimeoutException {
        this.queueName = queueName;
        connection = connectionFactory.newConnection();
        /*this will create a new channel, using an internally allocated channel number or we can say it will simply declare a queue for this channel. If queue does not exist.*/
        channel = connection.createChannel();

        channel.queueDeclare(queueName + TIMEOUT_SUFFIX, true, false, false, null);
        channel.exchangeDeclare(queueName + TIMEOUT_SUFFIX, "direct");

        channel.queueBind(queueName + TIMEOUT_SUFFIX, queueName + TIMEOUT_SUFFIX, "");

        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", queueName + TIMEOUT_SUFFIX);
        args.put("x-dead-letter-routing-key", "");
        channel.queueDeclare(queueName, true, false, false, args);
        channel.exchangeDeclare(queueName, "direct");
        channel.queueBind(queueName, queueName, "");
    }


    public Channel getChannel() {
        return channel;
    }


    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public String getQueueName() {
        return queueName;
    }

    public String getTimeoutQuename() {
        return getQueueName() + TIMEOUT_SUFFIX;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public void close() throws IOException, TimeoutException {
        this.channel.close();
        this.connection.close();
    }
}
