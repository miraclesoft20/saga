package ir.saga.participant;

import ir.saga.command.LockTarget;
import ir.saga.command.common.paths.PathVariables;
import ir.saga.command.consumer.CommandMessage;
import ir.saga.message.Message;

public interface PostLockFunction<C> {
    LockTarget apply(CommandMessage<C> cm, PathVariables pvs, Message reply);
}
