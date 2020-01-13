package ir.saga.repository;

import ir.saga.domain.SagaLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Optional;

public class SagaLockRepositoryImpl implements SagaLockRepositoryCustom {
    private final String COLLECTION = "sagaLock";

    @Qualifier("sagaMongoTemplate")
    @Autowired
    private MongoTemplate sagaMongoTemplate;


    public Long update(String sagaType, String sagaId, String target) {
        Query query = new Query();
        Update update = new Update();
        update.addToSet("id", sagaId);
        update.addToSet("sagaType", sagaType);
        query.addCriteria(Criteria.where("target").is(target));
        Long count = sagaMongoTemplate.count(query, COLLECTION);

        sagaMongoTemplate.findAndModify(query, update, SagaLock.class);
        return count;
    }


    public Optional<SagaLock> findOneByTargetEquals(String target) {
        Query query = new Query();
        query.addCriteria(Criteria.where("target").is(target));
        return Optional.ofNullable(sagaMongoTemplate.findOne(query, SagaLock.class, COLLECTION));
    }

}
