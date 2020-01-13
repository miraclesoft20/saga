package ir.saga.orchestration;


import ir.saga.common.SagaData;
import ir.saga.message.Message;

public interface SagaDefinition<Data extends SagaData> {
    SagaActions<Data> start(Data sagaData);

    SagaActions<Data> handleReply(String currentState, Data sagaData, Message message);

}
