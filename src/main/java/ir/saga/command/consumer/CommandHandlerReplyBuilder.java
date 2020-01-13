package ir.saga.command.consumer;

import ir.saga.command.common.CommandReplyOutcome;
import ir.saga.command.common.Failure;
import ir.saga.command.common.ReplyMessageHeaders;
import ir.saga.command.common.Success;
import ir.saga.message.Message;
import ir.saga.message.MessageBuilder;
import ir.saga.util.JSonMapper;

import java.util.Map;

public class CommandHandlerReplyBuilder {
    private static <T> Message with(T reply, Map<String, String> headers, CommandReplyOutcome outcome) {
        MessageBuilder messageBuilder = MessageBuilder
                .withPayload(JSonMapper.toJson(reply))
                .withHeader(ReplyMessageHeaders.REPLY_OUTCOME, outcome.name())
                .withHeader(ReplyMessageHeaders.REPLY_TYPE, reply.getClass().getName())
                .withHeader(Message.ID, headers.get(Message.ID))
                .withHeader(Message.CLIENT_IP, headers.get(Message.CLIENT_IP));

        return messageBuilder.build(headers.get(Message.SECURITY_TOKEN));
    }

    public static Message withSuccess(Object reply, Map<String, String> headers) {
        return with(reply, headers, CommandReplyOutcome.SUCCESS);
    }

    public static Message withSuccess(Map<String, String> headers) {
        return withSuccess(new Success(), headers);
    }

    public static Message withFailure(Map<String, String> headers) {
        return withFailure(new Failure(), headers);
    }

    public static Message withFailure(Object reply, Map<String, String> headers) {
        return with(reply, headers, CommandReplyOutcome.FAILURE);
    }
}
