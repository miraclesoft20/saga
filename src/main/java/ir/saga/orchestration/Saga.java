package ir.saga.orchestration;

import ir.saga.common.SagaData;

public abstract class Saga<Data extends SagaData> {

    protected abstract SagaDefinition<Data> getSagaDefinition();

    String getSagaType() {
        return this.getClass().getName();
    }

    /**
     * this method must return reply channel name (in rabbitmq queue name)
     *
     * @return
     */
    protected abstract String getListenerChannel();

    protected void onStarting(String sagaId, Data data) {
    }

    protected void onSagaCompletedSuccessfully(String Long, Data data) {
    }

    protected void onSagaRolledBack(String sagaId, Data data) {
    }

}
