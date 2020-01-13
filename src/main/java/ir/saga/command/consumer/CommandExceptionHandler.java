package ir.saga.command.consumer;

import ir.saga.message.Message;

import java.util.List;

public class CommandExceptionHandler {
    public List<Message> invoke(Throwable cause) {
        throw new UnsupportedOperationException(cause);
    }
}
