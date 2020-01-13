package ir.saga.repository;

import ir.saga.domain.SagaLock;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface SagaLockRepository extends MongoRepository<SagaLock, String>,SagaLockRepositoryCustom {

    Optional<SagaLock> findOneByTargetEquals(String target);

}
