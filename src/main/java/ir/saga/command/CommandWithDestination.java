package ir.saga.command;

public class CommandWithDestination {
    private final String destinationChannel;
    private final String resource;
    private final Command command;

    public CommandWithDestination(String destinationChannel, String resource, Command command) {
        this.destinationChannel = destinationChannel;
        this.resource = resource;
        this.command = command;
    }

    public String getDestinationChannel() {
        return destinationChannel;
    }

    public String getResource() {
        return resource;
    }

    public Command getCommand() {
        return command;
    }
}
