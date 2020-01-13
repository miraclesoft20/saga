package ir.saga.rabbitmq;

import com.rabbitmq.client.ConnectionFactory;
import ir.saga.events.DomainEventPublisher;
import ir.saga.message.consumer.MessageConsumer;
import ir.saga.message.producer.MessageProducer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class RabbitConfiguration {
    @Value("${saga.rabbitmq.host}")
    private String host;
    @Value("${saga.rabbitmq.port}")
    private Integer port;
    @Value("${saga.rabbitmq.username}")
    private String username;
    @Value("${saga.rabbitmq.password}")
    private String password;

    @Bean("sagaConnectionFactory")
    ConnectionFactory connectionFactory() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setPort(port);
        factory.setUsername(username);
        factory.setPassword(password);
        return factory;
    }

    @Bean
    MessageConsumer messageConsumer(@Qualifier("sagaConnectionFactory") ConnectionFactory connectionFactory) {
        return new MessageConsumerRabbitImpl(connectionFactory);
    }

    @Bean
    MessageProducer messageProducer(@Qualifier("sagaConnectionFactory") ConnectionFactory connectionFactory, DomainEventPublisher domainEventPublisher){
        return  new MessageProducerRabbitImpl(connectionFactory, domainEventPublisher);
    }
}
