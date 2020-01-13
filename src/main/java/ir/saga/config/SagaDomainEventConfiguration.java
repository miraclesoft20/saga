package ir.saga.config;


import ir.saga.events.DomainEventBus;
import ir.saga.events.DomainEventBusImpl;
import ir.saga.events.DomainEventPublisher;
import ir.saga.events.DomainEventPublisherImpl;
import org.springframework.context.annotation.Bean;

public class SagaDomainEventConfiguration {

    @Bean
    public DomainEventBus domainEventBus(){
        return new DomainEventBusImpl();
    }
    @Bean
    public DomainEventPublisher domainEventPublisher(DomainEventBus bus){
        return new DomainEventPublisherImpl(bus);
    }
}
