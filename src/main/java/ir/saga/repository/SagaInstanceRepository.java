package ir.saga.repository;


import ir.saga.domain.SagaInstance;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SagaInstanceRepository extends MongoRepository<SagaInstance, String>, SagaInstanceRepositoryCustom {



   /* SagaInstance save(SagaInstance sagaInstance);
    SagaInstance update(SagaInstance sagaInstance);*/
}
