package ir.saga.orchestration;

import ir.saga.common.SagaData;
import ir.saga.domain.SagaInstance;

public class SagaInstanceData <Data extends SagaData> {

    private final SagaInstance sagaInstance;
    private final Data sagaData;

    public SagaInstanceData(SagaInstance sagaInstance, Data sagaData) {
        this.sagaInstance = sagaInstance;
        this.sagaData = sagaData;
    }

    public SagaInstance getSagaInstance() {
        return sagaInstance;
    }

    public Data getSagaData() {
        return sagaData;
    }
}
