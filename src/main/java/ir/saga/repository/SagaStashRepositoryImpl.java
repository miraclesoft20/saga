package ir.saga.repository;

import ir.saga.domain.SagaStash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.stream.Stream;


public class SagaStashRepositoryImpl implements SagaStashRepositoryCustom{
 private final String COLLECTION ="sagaStash";
    @Qualifier("sagaMongoTemplate")
    @Autowired
    private MongoTemplate sagaMongoTemplate;


    public Stream<SagaStash> findAllBySagaIdEqualsAndTargetEquals(String sagaId, String target) {
        Query query = new Query();
        query.addCriteria(Criteria.where("sagaId").is(sagaId).and("target").is(target));
        return sagaMongoTemplate.find(query,SagaStash.class).stream();
    }

    @Override
    public Long deleteSagaStashByTarget(String target) {
        Query query = new Query();
        query.addCriteria(Criteria.where("target").is(target));
        Long count = sagaMongoTemplate.count(query,SagaStash.class);
        sagaMongoTemplate.remove(query,SagaStash.class);

        return count;
    }

    @Override
    public Long deleteSagaStashByMessageId(String messageId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("messageId").is(messageId));
        Long count = sagaMongoTemplate.count(query,SagaStash.class);
         sagaMongoTemplate.remove(query,SagaStash.class);
      return count;
    }


}
