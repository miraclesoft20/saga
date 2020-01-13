package ir.saga.message.consumer;

import ir.saga.message.Message;

import java.util.function.BiConsumer;

public interface MessageHandler extends BiConsumer<String, Message> {
}
