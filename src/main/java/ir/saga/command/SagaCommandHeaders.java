package ir.saga.command;

import ir.saga.command.common.CommandMessageHeaders;

public class SagaCommandHeaders {
    public static final String SAGA_TYPE = CommandMessageHeaders.COMMAND_HEADER_PREFIX + "saga_type";
    public static final String SAGA_ID = CommandMessageHeaders.COMMAND_HEADER_PREFIX + "saga_id";
}
