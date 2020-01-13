package ir.saga.message.consumer;


import ir.saga.message.interceptor.MessagePostInterceptor;
import ir.saga.message.interceptor.MessagePreInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.TimeoutException;

public abstract class MessageConsumer {

    @Lazy
    @Autowired(required = false)
    protected Set<MessagePreInterceptor> preInterceptors;

    @Lazy
    @Autowired(required = false)
    protected Set<MessagePostInterceptor> postInterceptor;

   public abstract void subscribe(String subscriberId, Set<String> channelNames, MessageHandler handler);

    public abstract void close() throws IOException, TimeoutException;
}
