package ir.saga.config;

import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "saga.mongodb")
public class SagaMongoConnectionProperties {
    private MongoProperties properties = new MongoProperties();

    public MongoProperties getProperties() {
        return properties;
    }

    public void setProperties(MongoProperties properties) {
        this.properties = properties;
    }
}
