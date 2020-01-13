package ir.saga.events;

import java.util.List;

public interface DomainEventPublisher {
    void publish(String aggregateType, Object aggregateId, List<MessageTimeoutEvent> domainEvents);
}
