package ir.saga.simpledsl;

import ir.saga.command.Command;
import ir.saga.command.CommandWithDestination;
import ir.saga.common.SagaData;

import java.util.function.Function;
import java.util.function.Predicate;

public interface WithCompensationBuilder<Data extends SagaData> {

    InvokeParticipantStepBuilder<Data> withCompensation(Function<Data, CommandWithDestination> compensation);

    InvokeParticipantStepBuilder<Data> withCompensation(Predicate<Data> compensationPredicate, Function<Data, CommandWithDestination> compensation);

    <C extends Command> InvokeParticipantStepBuilder<Data> withCompensation(CommandEndpoint<C> commandEndpoint, Function<Data, C> commandProvider);

    <C extends Command> InvokeParticipantStepBuilder<Data> withCompensation(Predicate<Data> compensationPredicate, CommandEndpoint<C> commandEndpoint, Function<Data, C> commandProvider);
}
