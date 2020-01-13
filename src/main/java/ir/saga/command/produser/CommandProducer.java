package ir.saga.command.produser;


import ir.saga.command.Command;

import java.util.Map;

public interface CommandProducer {
    /**
     * Sends a command
     *
     * @param channel
     * @param command the command to doSend
     * @param replyTo
     * @param headers additional headers  @return the id of the sent command
     */
    String send(String channel, Command command, String replyTo, Map<String, String> headers, String securityToken);

    /**
     * Sends a command
     *
     * @param channel
     * @param resource
     * @param command  the command to doSend
     * @param replyTo
     * @param headers  additional headers  @return the id of the sent command
     */
    String send(String channel, String resource, Command command, String replyTo, Map<String, String> headers, String securityToken);

    String sendWithTimoutListener(String channel, String resource, Command command, String replyTo, Map<String, String> headers, String securityToken);
}
