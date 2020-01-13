package ir.saga.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DomainEventBusImpl implements DomainEventBus {

    private Map<String, List<DomainEventHandler>> subscriptions = new HashMap<>();

    @Override
    public void subscribe(String aggregateType, DomainEventHandler handler) {
        List<DomainEventHandler> handlers = subscriptions.get(aggregateType);
        if (handlers == null) {
            handlers = new ArrayList<>();
            subscriptions.put(aggregateType, handlers);
        }
        handlers.add(handler);
    }

    @Override
    public void push(String aggregateType, Object aggregateId, List<MessageTimeoutEvent> domainEvents) {
        List<DomainEventHandler> handlers = subscriptions.get(aggregateType);
        for (DomainEventHandler handler : handlers)
            handler.accept(domainEvents);
    }

    @Override
    public void close() {
        subscriptions.clear();
    }
}
