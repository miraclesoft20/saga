package ir.saga.simpledsl;

import ir.saga.command.Command;
import ir.saga.command.CommandWithDestination;
import ir.saga.command.common.CommandReplyOutcome;
import ir.saga.command.common.ReplyMessageHeaders;
import ir.saga.message.Message;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class ParticipantEndpointInvocationImpl<Data, C extends Command> extends AbstractParticipantInvocation<Data> {


    private final CommandEndpoint<C> commandEndpoint;
    private final Function<Data, C> commandProvider;

    public ParticipantEndpointInvocationImpl(Optional<Predicate<Data>> invocablePredicate, CommandEndpoint<C> commandEndpoint, Function<Data, C> commandProvider) {
        super(invocablePredicate);
        this.commandEndpoint = commandEndpoint;
        this.commandProvider = commandProvider;
    }

    @Override
    public boolean isSuccessfulReply(Message message) {
        return CommandReplyOutcome.SUCCESS.name().equals(message.getRequiredHeader(ReplyMessageHeaders.REPLY_OUTCOME));
    }

    @Override
    public CommandWithDestination makeCommandToSend(Data data) {
        return new CommandWithDestination(commandEndpoint.getCommandChannel(), null, commandProvider.apply(data));
    }
}
