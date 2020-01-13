package ir.saga.repository;

import ir.saga.common.SagaData;
import ir.saga.domain.SagaInstance;
import ir.saga.orchestration.SagaInstanceData;

public interface SagaInstanceRepositoryCustom {
    <Data extends SagaData> SagaInstanceData<Data> findWithData(String sagaType, String sagaId);

    SagaInstance findBySagaTypeAndId(String sagaType, String sagaId);
}
