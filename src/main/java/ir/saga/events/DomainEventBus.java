package ir.saga.events;

import java.util.List;
public interface DomainEventBus {
    void subscribe(String aggregateType,DomainEventHandler consumer);
    void push(String aggregateType, Object aggregateId, List<MessageTimeoutEvent> domainEvents);
    void close();
}
