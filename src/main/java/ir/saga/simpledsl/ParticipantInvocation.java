package ir.saga.simpledsl;

import ir.saga.command.CommandWithDestination;
import ir.saga.message.Message;

public interface   ParticipantInvocation<Data> {
        boolean isSuccessfulReply(Message message);

        CommandWithDestination makeCommandToSend(Data data);

        boolean isInvocable(Data data);

}
