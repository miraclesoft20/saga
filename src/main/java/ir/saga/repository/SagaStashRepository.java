package ir.saga.repository;

import ir.saga.domain.SagaStash;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.stream.Stream;

@Repository
public interface SagaStashRepository extends MongoRepository<SagaStash, String>, SagaStashRepositoryCustom {
    Stream<SagaStash> findAllBySagaIdEqualsAndTargetEquals(String sagaId, String target);
}
