package ir.saga.repository;


import ir.saga.orchestration.SagaInstanceData;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ir.saga.domain.SagaInstance;

@Repository
public interface SagaInstanceRepository   extends MongoRepository<SagaInstance, String>,SagaInstanceRepositoryCustom {



   /* SagaInstance save(SagaInstance sagaInstance);
    SagaInstance update(SagaInstance sagaInstance);*/
}
