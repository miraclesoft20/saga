package ir.saga.events;


import java.util.List;

public class DomainEventPublisherImpl implements  DomainEventPublisher{

   private final DomainEventBus eventBus;

    public DomainEventPublisherImpl(DomainEventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void publish(String aggregateType, Object aggregateId, List<MessageTimeoutEvent> domainEvents) {
        eventBus.push(aggregateType,aggregateId,domainEvents);
    }
}
