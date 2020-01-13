package ir.saga.orchestration;

import ir.saga.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import ir.saga.command.Command;
import ir.saga.command.CommandWithDestination;
import ir.saga.command.produser.CommandProducer;
import ir.saga.command.SagaCommandHeaders;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SagaCommandProducer {
    @Autowired
    private CommandProducer commandProducer;

    public SagaCommandProducer() {
    }

    public SagaCommandProducer(CommandProducer commandProducer) {
        this.commandProducer = commandProducer;
    }



    public String sendCommand(String sagaType, String sagaId, String destinationChannel, String resource, Command command, String replyTo,String securityToken,String clientIp) {
        Map<String, String> headers = new HashMap<>();
        headers.put(SagaCommandHeaders.SAGA_TYPE, sagaType);
        headers.put(SagaCommandHeaders.SAGA_ID, sagaId);
        headers.put(Message.CLIENT_IP, clientIp);
        return commandProducer.sendWithTimoutListener(destinationChannel, resource, command, replyTo, headers,securityToken);
    }

    public String sendCommands(String sagaType, String sagaId, List<CommandWithDestination> commands, String sagaReplyChannel,String securityToken,String clientIp) {
        return commands.stream().map(command -> sendCommand(sagaType, sagaId, command.getDestinationChannel(), command.getResource(),
                command.getCommand(), sagaReplyChannel,securityToken,clientIp)).reduce( (a, b) -> b).orElse(null);

    }

    public String directSend(String destinationChannel, String resource, Command command, String replyTo,Map<String,String> headers,String securityToken){
        return commandProducer.send(destinationChannel, resource, command, replyTo, headers,securityToken);
    }
}
