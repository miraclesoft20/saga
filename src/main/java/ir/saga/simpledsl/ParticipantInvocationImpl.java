package ir.saga.simpledsl;

import ir.saga.command.Command;
import ir.saga.command.CommandWithDestination;
import ir.saga.command.common.CommandReplyOutcome;
import ir.saga.command.common.ReplyMessageHeaders;
import ir.saga.message.Message;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class ParticipantInvocationImpl <Data, C extends Command> extends AbstractParticipantInvocation<Data> {
    private Function<Data, CommandWithDestination> commandBuilder;


    public ParticipantInvocationImpl(Optional<Predicate<Data>> invocablePredicate, Function<Data, CommandWithDestination> commandBuilder) {
        super(invocablePredicate);
        this.commandBuilder = commandBuilder;
    }

    @Override
    public boolean isSuccessfulReply(Message message) {
        return CommandReplyOutcome.SUCCESS.name().equals(message.getRequiredHeader(ReplyMessageHeaders.REPLY_OUTCOME));
    }

    @Override
    public CommandWithDestination makeCommandToSend(Data data) {
        return commandBuilder.apply(data);
    }
}
