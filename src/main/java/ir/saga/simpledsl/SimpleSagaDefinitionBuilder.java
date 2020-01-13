package ir.saga.simpledsl;

import ir.saga.common.SagaData;
import ir.saga.orchestration.SagaDefinition;

import java.util.LinkedList;
import java.util.List;

public class SimpleSagaDefinitionBuilder<Data extends SagaData> {

    private List<SagaStep<Data>> sagaSteps = new LinkedList<>();

    public void addStep(SagaStep<Data> sagaStep) {
        sagaSteps.add(sagaStep);
    }

    public SagaDefinition<Data> build() {
        return new SimpleSagaDefinition<>(sagaSteps);
    }
}
