package ir.saga.participant;

import ir.saga.command.consumer.CommandHandlers;
import ir.saga.command.SagaLockManager;
import ir.saga.message.consumer.MessageConsumer;
import ir.saga.message.producer.MessageProducer;

public class SagaCommandDispatcherFactory {
    private final MessageConsumer messageConsumer;
    private final MessageProducer messageProducer;
    private final SagaLockManager sagaLockManager;

    public SagaCommandDispatcherFactory(MessageConsumer messageConsumer, MessageProducer messageProducer, SagaLockManager sagaLockManager) {
        this.messageConsumer = messageConsumer;
        this.messageProducer = messageProducer;
        this.sagaLockManager = sagaLockManager;
    }

    public SagaCommandDispatcher make(String commandDispatcherId, CommandHandlers target) {
        return new SagaCommandDispatcher(commandDispatcherId, target, messageConsumer, messageProducer, sagaLockManager);
    }
}
