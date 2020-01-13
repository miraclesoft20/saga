package ir.saga.simpledsl;

import ir.saga.command.common.CommandReplyOutcome;
import ir.saga.command.common.ReplyMessageHeaders;
import ir.saga.common.SagaData;
import ir.saga.message.Message;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static ir.saga.simpledsl.StepOutcome.makeLocalOutcome;

public class LocalStep<Data extends SagaData> implements SagaStep<Data> {
    private Consumer<Data> localFunction;
    private Optional<Consumer<Data>> compensation;

    public LocalStep(Consumer<Data> localFunction, Optional<Consumer<Data>> compensation) {
        this.localFunction = localFunction;
        this.compensation = compensation;
    }

    @Override
    public boolean hasAction(Data data) {
        return true;
    }

    @Override
    public boolean hasCompensation(Data data) {
        return compensation.isPresent();
    }


    @Override
    public boolean isSuccessfulReply(boolean compensating, Message message) {
        return CommandReplyOutcome.SUCCESS.name().equals(message.getRequiredHeader(ReplyMessageHeaders.REPLY_OUTCOME));
    }

    @Override
    public Optional<BiConsumer<Data, Object>> getReplyHandler(Message message, boolean compensating) {
        return Optional.empty();
    }


    @Override
    public StepOutcome makeStepOutcome(Data data, boolean compensating) {
        try {
            if (compensating) {
                compensation.ifPresent(localStep -> localStep.accept(data));
            } else {
                localFunction.accept(data);
            }
            return makeLocalOutcome(Optional.empty());
        } catch (RuntimeException e) {
            return makeLocalOutcome(Optional.of(e));
        }
    }
}
