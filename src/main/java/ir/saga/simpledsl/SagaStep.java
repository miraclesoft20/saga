package ir.saga.simpledsl;

import ir.saga.common.SagaData;
import ir.saga.message.Message;

import java.util.Optional;
import java.util.function.BiConsumer;

public interface SagaStep<Data extends SagaData> {
    boolean isSuccessfulReply(boolean compensating, Message message);

    Optional<BiConsumer<Data, Object>> getReplyHandler(Message message, boolean compensating);

    StepOutcome makeStepOutcome(Data data, boolean compensating);

    boolean hasAction(Data data);

    boolean hasCompensation(Data data);
}
