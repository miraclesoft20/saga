package ir.saga.simpledsl;

import ir.saga.common.SagaData;

public abstract class SimpleSagaDsl<Data extends SagaData> {

    protected StepBuilder<Data> step() {
        SimpleSagaDefinitionBuilder<Data> builder = new SimpleSagaDefinitionBuilder<>();
        return new StepBuilder<>(builder);
    }
}
