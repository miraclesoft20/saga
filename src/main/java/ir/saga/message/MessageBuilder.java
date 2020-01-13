package ir.saga.message;

import java.util.HashMap;
import java.util.Map;

public class MessageBuilder {

    protected String body;
    protected Map<String, String> headers = new HashMap<>();

    protected MessageBuilder() {
    }

    public MessageBuilder(String body) {
        this.body = body;
    }

    public MessageBuilder(Message message) {
        this(message.getPayload());
        this.headers = message.getHeaders();
    }

    public static MessageBuilder withPayload(String payload) {
        return new MessageBuilder(payload);
    }

    public MessageBuilder withHeader(String name, String value) {
        this.headers.put(name, value);
        return this;
    }


    public MessageBuilder withHeader(Map<String, String> headers) {
        if (headers != null && headers.size() > 0) {
            this.headers.keySet().removeAll(headers.keySet());
            this.headers.putAll(headers);
        }
        return this;
    }

    public MessageBuilder withExtraHeaders(String prefix, Map<String, String> headers) {

        for (Map.Entry<String, String> entry : headers.entrySet())
            this.headers.put(prefix + entry.getKey(), entry.getValue());

        return this;
    }

    public Message build(String securityToken) {
        if (headers == null) {
            headers = new HashMap<>();
        }
        if (this.headers.containsKey(Message.SECURITY_TOKEN)) {
            this.headers.remove(Message.SECURITY_TOKEN);
        }

        this.headers.put(Message.SECURITY_TOKEN, securityToken);
        return new MessageImpl(body, headers);
    }


    public Message build() {
        return new MessageImpl(body, headers);
    }

    public static MessageBuilder withMessage(Message message) {
        return new MessageBuilder(message);
    }
}
