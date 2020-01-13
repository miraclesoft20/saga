package ir.saga.common;

import ir.saga.command.SagaCommandHeaders;
import ir.saga.command.common.CommandMessageHeaders;

public class SagaReplyHeaders {

    public static final String REPLY_SAGA_TYPE = CommandMessageHeaders.inReply(SagaCommandHeaders.SAGA_TYPE);
    public static final String REPLY_SAGA_ID = CommandMessageHeaders.inReply(SagaCommandHeaders.SAGA_ID);

    public static final String REPLY_LOCKED = "saga-locked-target";
}
