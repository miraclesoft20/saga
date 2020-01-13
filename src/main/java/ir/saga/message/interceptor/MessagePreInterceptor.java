package ir.saga.message.interceptor;

import ir.saga.message.Message;

public interface MessagePreInterceptor {
    void doInterceptor(Message message);
}
