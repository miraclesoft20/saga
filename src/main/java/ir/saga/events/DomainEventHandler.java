package ir.saga.events;

import java.util.List;
import java.util.function.Consumer;

public interface DomainEventHandler extends Consumer<List<MessageTimeoutEvent>> {
}
