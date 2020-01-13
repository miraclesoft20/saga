package ir.saga.config;

import ir.saga.command.SagaLockManager;
import ir.saga.command.SagaLockManagerImpl;
import ir.saga.message.consumer.MessageConsumer;
import ir.saga.message.producer.MessageProducer;
import ir.saga.participant.SagaCommandDispatcherFactory;
import ir.saga.repository.SagaLockRepository;
import ir.saga.repository.SagaStashRepository;
import org.springframework.context.annotation.Bean;


public class SagaParticipantConfiguration {
    @Bean
    public SagaCommandDispatcherFactory sagaCommandDispatcherFactory(MessageConsumer messageConsumer, MessageProducer messageProducer, SagaLockManager sagaLockManager) {
        return new SagaCommandDispatcherFactory(messageConsumer, messageProducer, sagaLockManager);
    }

    @Bean
    public SagaLockManager sagaLockManager(SagaStashRepository sagaStashRepository, SagaLockRepository sagaLockRepository) {
        return new SagaLockManagerImpl(sagaLockRepository, sagaStashRepository);
    }
}
