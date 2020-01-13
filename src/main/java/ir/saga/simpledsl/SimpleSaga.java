package ir.saga.simpledsl;

import ir.saga.common.SagaData;
import ir.saga.orchestration.SagaDefinition;

public abstract class SimpleSaga<Data extends SagaData> {

    protected abstract SagaDefinition<Data> getSagaDefinition();

    protected StepBuilder<Data> step() {
        SimpleSagaDefinitionBuilder<Data> builder = new SimpleSagaDefinitionBuilder<>();
        return new StepBuilder<>(builder);
    }

    protected String getSagaType() {
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
