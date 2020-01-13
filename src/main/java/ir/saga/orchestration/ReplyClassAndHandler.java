package ir.saga.orchestration;


public interface ReplyClassAndHandler {
    RawSagaStateMachineAction getReplyHandler();
    Class<?> getReplyClass();
}
