package ir.saga.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import({SagaDatabaseConfiguration.class, SagaParticipantConfiguration.class, SagaDomainEventConfiguration.class})
@ComponentScan(basePackages = "ir.saga.*")

@Configuration
public class SagaConfiguration {
}
