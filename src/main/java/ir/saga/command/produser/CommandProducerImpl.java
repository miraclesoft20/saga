package ir.saga.command.produser;

import ir.saga.command.Command;
import ir.saga.command.common.CommandMessageHeaders;
import ir.saga.message.common.IdGenerator;
import ir.saga.message.Message;
import ir.saga.message.MessageBuilder;
import ir.saga.message.producer.MessageProducer;
import ir.saga.util.JSonMapper;

import java.util.Map;

public class CommandProducerImpl implements CommandProducer {

    private final MessageProducer messageProducer;
    private final IdGenerator idGenerator;

    public CommandProducerImpl(MessageProducer messageProducer, IdGenerator idGenerator) {
        this.messageProducer = messageProducer;
        this.idGenerator = idGenerator;
    }

    @Override
    public String send(String channel, Command command, String replyTo, Map<String, String> headers,String securityToken) {
        return send(channel, null, command, replyTo, headers,securityToken);
    }

    @Override
    public String send(String channel, String resource, Command command, String replyTo, Map<String, String> headers,String securityToken) {
        Message message = makeMessage(channel, resource, command, replyTo, headers,securityToken);
        message.setHeader(Message.ID,idGenerator.genId().asString());
        messageProducer.send(channel, message);
        return message.getId();
    }

    @Override
    public String sendWithTimoutListener(String channel, String resource, Command command, String replyTo, Map<String, String> headers, String securityToken) {
        Message message = makeMessage(channel, resource, command, replyTo, headers,securityToken);
        message.setHeader(Message.ID,idGenerator.genId().asString());
        messageProducer.sendWithTimeoutListener(channel, message);
        return message.getId();

    }

    public static Message makeMessage(String channel, String resource, Command command, String replyTo, Map<String, String> headers, String securityToken) {
        MessageBuilder builder = MessageBuilder.withPayload(JSonMapper.toJson(command))
                .withExtraHeaders("", headers) // TODO should these be prefixed??!
                .withHeader(CommandMessageHeaders.DESTINATION, channel)
                .withHeader(CommandMessageHeaders.COMMAND_TYPE, command.getClass().getName())
                .withHeader(CommandMessageHeaders.REPLY_TO, replyTo);


        if (resource != null)
            builder.withHeader(CommandMessageHeaders.RESOURCE, resource);

        return builder
                .build(securityToken);
    }
}
