package ir.saga.orchestration;

public interface RawSagaStateMachineAction {
    SagaActions apply(Object sagaData, Object reply);
}
