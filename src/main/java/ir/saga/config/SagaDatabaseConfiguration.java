package ir.saga.config;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Collections;

@Configuration
@EnableConfigurationProperties(SagaMongoConnectionProperties.class)
@EnableMongoRepositories(basePackages = "ir.saga.repository", mongoTemplateRef = "sagaMongoTemplate")
@EntityScan(basePackages = "ir.saga.domain")
public class SagaDatabaseConfiguration {
    private int socketTimeout = 1000 * 30;
    private int connectionTimeout = 1000 * 30;
    @Autowired
    private SagaMongoConnectionProperties sagaMongoConnectionProperties;

    @Primary
    @Bean(name = {"sagaMongoTemplate"})
    public MongoTemplate mongoTemplate(@Qualifier("sagaMongoDbFactory") MongoDbFactory mongoDbFactory) {
        return new MongoTemplate(mongoDbFactory);
    }


    @Primary
    @Bean(name = "sagaMongoDbFactory")
    public MongoDbFactory mongoDbFactory(@Qualifier("sagaMongoClient") MongoClient mongoClient) {
        return new SimpleMongoDbFactory(mongoClient, this.sagaMongoConnectionProperties.getProperties().getDatabase());

    }

    @Primary
    @Bean(name = "sagaMongoClient")
    public MongoClient sagaMongoClient() {

        MongoClientOptions options = MongoClientOptions.builder().socketTimeout(socketTimeout).connectTimeout(connectionTimeout).build();
        return new MongoClient(new ServerAddress(this.sagaMongoConnectionProperties.getProperties().getHost(),
                this.sagaMongoConnectionProperties.getProperties().getPort()),
                Collections.singletonList(MongoCredential.createCredential(this.sagaMongoConnectionProperties.getProperties().getUsername(),
                        this.sagaMongoConnectionProperties.getProperties().getDatabase(),
                        this.sagaMongoConnectionProperties.getProperties().getPassword())), options);
    }

  /*  @Primary
    @Bean
    public CustomConversions customConversions(ZonedDateTimeToDocumentConverter zonedDateTimeToDocumentConverter,DocumentToZonedDateTimeConverter documentToZonedDateTimeConverter) {
        return new MongoCustomConversions(Arrays.asList(
                zonedDateTimeToDocumentConverter,
                documentToZonedDateTimeConverter
        ));
    }

    @Bean
    public ZonedDateTimeToDocumentConverter zonedDateTimeToDocumentConverter(){
        return  new ZonedDateTimeToDocumentConverter();
    }

    @Bean
    public DocumentToZonedDateTimeConverter documentToZonedDateTimeConverter(){
        return  new DocumentToZonedDateTimeConverter();
    }*/


}
