package ir.saga.repository;



import ir.saga.common.SagaData;
import ir.saga.domain.SagaInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.mongodb.core.MongoTemplate;
import ir.saga.orchestration.SagaInstanceData;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

public class SagaInstanceRepositoryImpl  implements SagaInstanceRepositoryCustom  {

    @Qualifier("sagaMongoTemplate")
    @Autowired()
    MongoTemplate  sagaMongoTemplate;


    public SagaInstance findBySagaTypeAndId(String sagaType, String sagaId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("sagaType").is(sagaType).and("id").is(sagaId));
        return sagaMongoTemplate.findOne(query,SagaInstance.class);
    }


    public SagaInstance update(SagaInstance sagaInstance) {
        Query query = new Query();
        Update update = new Update();
        update.addToSet("compensating",sagaInstance.getCompensating());
        update.addToSet("destinationsAndResources",sagaInstance.getDestinationsAndResources());
        update.addToSet("endState",sagaInstance.getEndState());
        update.addToSet("lastRequestId",sagaInstance.getLastRequestId());
        update.addToSet("sagaType",sagaInstance.getSagaType());
        update.addToSet("serializedSagaData",sagaInstance.getSerializedSagaData());
        update.addToSet("stateName",sagaInstance.getStateName());

        query.addCriteria(Criteria.where("id").is (sagaInstance.getId()));
        sagaMongoTemplate.findAndModify(query,update,SagaInstance.class);
        return null;
    }

    @Override
    public <Data extends SagaData> SagaInstanceData<Data> findWithData(String sagaType, String sagaId) {
        return null;
    }
}
