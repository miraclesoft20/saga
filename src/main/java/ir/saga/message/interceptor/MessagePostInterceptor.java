package ir.saga.message.interceptor;

import ir.saga.message.Message;

public interface MessagePostInterceptor {
    void doInterceptor(Message message);
}
