package ir.saga.orchestration;

import ir.saga.command.LockTarget;
import ir.saga.command.SagaCommandHeaders;
import ir.saga.command.SagaLockManager;
import ir.saga.command.SagaUnlockCommand;
import ir.saga.command.common.*;
import ir.saga.common.SagaData;
import ir.saga.common.SagaReplyHeaders;
import ir.saga.domain.SagaInstance;
import ir.saga.events.DomainEventBus;
import ir.saga.events.MessageTimeoutEvent;
import ir.saga.message.Message;
import ir.saga.message.MessageBuilder;
import ir.saga.message.consumer.MessageConsumer;
import ir.saga.repository.SagaInstanceRepository;
import ir.saga.simpledsl.SimpleSaga;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.time.ZonedDateTime;
import java.util.*;

import static java.util.Collections.singleton;

public abstract class SagaManagerImpl<Data extends SagaData> extends SimpleSaga<Data> implements SagaManager<Data> {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    private static final String DEFAULT_STATE_NAME = "{\"currentlyExecuting\":-1,\"compensating\":false,\"endState\":false}";
    @Autowired
    private SagaInstanceRepository sagaInstanceRepository;

    @Autowired
    private SagaCommandProducer sagaCommandProducer;
    @Autowired
    private MessageConsumer messageConsumer;

    @Autowired
    private SagaLockManager sagaLockManager;

    @Autowired
    private DomainEventBus eventBus;

    @PostConstruct
    public void subscribeToReplyChannel() {
        messageConsumer.subscribe(this.getSagaType() + "-consumer", singleton(makeSagaReplyChannel()),
                this::handleMessage);
        eventBus.subscribe(this.getSagaType(), this::handleMessageTimeoutEvent);
    }

    private String makeSagaReplyChannel() {
        return this.getListenerChannel();
    }


    @Override
    public SagaInstance create(Data sagaData, String currentUsername, String securityToken, String clientIp) {
        return create(sagaData, Optional.empty(), currentUsername, securityToken, clientIp);
    }

    @Override
    public SagaInstance create(Data data, Class targetClass, Object targetId, String currentUsername, String securityToken, String clientIp) {
        return create(data, Optional.of(new LockTarget(targetClass, targetId).getTarget()), currentUsername, securityToken, clientIp);
    }

    @Override
    public SagaInstance create(Data sagaData, Optional<String> resource, String currentUsername, String securityToken, String clientIp) {


        SagaInstance sagaInstance = new SagaInstance(getSagaType(),
                UUID.randomUUID().toString(),
                DEFAULT_STATE_NAME,
                null,
                SagaDataSerde.serializeSagaData(sagaData), new HashSet<>(), ZonedDateTime.now().toInstant(), currentUsername);

        sagaInstanceRepository.save(sagaInstance);

        String sagaId = sagaInstance.getId();

        this.onStarting(sagaId, sagaData);


        resource.ifPresent(r -> Assert.isTrue(sagaLockManager.claimLock(getSagaType(), sagaId, r), "Cannot claim lock for resource"));

        SagaActions<Data> actions = getStateDefinition().start(sagaData);

        actions.getLocalException().ifPresent(e -> {
            throw e;
        });

        processActions(sagaId, sagaInstance, sagaData, actions, securityToken, clientIp);

        return sagaInstance;
    }


    private SagaDefinition<Data> getStateDefinition() {
        SagaDefinition<Data> sm = this.getSagaDefinition();
        Assert.notNull(sm, "state machine cannot be null");
        return sm;
    }


    private void performEndStateActions(String sagaId, SagaInstance sagaInstance, boolean compensating, Data sagaData, String securityToken) {
        for (DestinationAndResource dr : sagaInstance.getDestinationsAndResources()) {
            Map<String, String> headers = new HashMap<>();
            headers.put(SagaCommandHeaders.SAGA_ID, sagaId);
            headers.put(SagaCommandHeaders.SAGA_TYPE, getSagaType()); // FTGO SagaCommandHandler failed without this but the OrdersAndCustomersIntegrationTest was fine?!?

            sagaCommandProducer.directSend(dr.getDestination(), dr.getResource(), new SagaUnlockCommand(), makeSagaReplyChannel(), headers, securityToken);

            if (compensating)
                this.onSagaRolledBack(sagaId, sagaData);
            else
                this.onSagaCompletedSuccessfully(sagaId, sagaData);
        }
    }


    public void handleMessage(String channel, Message message) {
        if (getListenerChannel().equals(channel)) {
            logger.debug("handle message invoked {}", message);
            if (message.hasHeader(SagaReplyHeaders.REPLY_SAGA_ID)) {
                handleReply(message);
            } else {
                logger.warn("Handle message doesn't know what to do with: {} ", message);
            }
        } else {
            handleReplyTimeout(message);
        }

    }


    private void handleMessageTimeoutEvent(List<MessageTimeoutEvent> timeoutEvents) {

        for (MessageTimeoutEvent timeoutEvent : timeoutEvents) {
            Message message = timeoutEvent.getMessage();
            message.setHeader(SagaReplyHeaders.REPLY_SAGA_ID, message.getRequiredHeader(SagaCommandHeaders.SAGA_ID));
            message.setHeader(SagaReplyHeaders.REPLY_SAGA_TYPE, message.getRequiredHeader(SagaCommandHeaders.SAGA_TYPE));
            message.setHeader(ReplyMessageHeaders.REPLY_OUTCOME, CommandReplyOutcome.FAILURE.name());
            message.setHeader(ReplyMessageHeaders.REPLY_TYPE, TimeoutFailure.class.getName());
            handleReply(message);
        }
    }

    private void handleReplyTimeout(Message message) {
        logger.debug("timeout for {}", message);
    }


    private void handleReply(Message message) {

        if (!isReplyForThisSagaType(message))
            return;

        logger.debug("Handle reply: {}", message);

        String sagaId = message.getRequiredHeader(SagaReplyHeaders.REPLY_SAGA_ID);
        String sagaType = message.getRequiredHeader(SagaReplyHeaders.REPLY_SAGA_TYPE);

        SagaInstance sagaInstance = sagaInstanceRepository.findBySagaTypeAndId(sagaType, sagaId);
        Data sagaData = SagaDataSerde.deserializeSagaData(sagaInstance.getSerializedSagaData());


        message.getHeader(SagaReplyHeaders.REPLY_LOCKED).ifPresent(lockedTarget -> {
            String destination = message.getRequiredHeader(CommandMessageHeaders.inReply(CommandMessageHeaders.DESTINATION));
            sagaInstance.addDestinationsAndResources(singleton(new DestinationAndResource(destination, lockedTarget)));
        });

        String currentState = sagaInstance.getStateName();

        logger.info("Current state={}", currentState);

        SagaActions<Data> actions = getStateDefinition().handleReply(currentState, sagaData, message);

        logger.info("Handled reply. Sending commands {}", actions.getCommands());

        processActions(sagaId, sagaInstance, sagaData, actions, message.getSecurityToken(), message.getClientIp());


    }

    private void processActions(String sagaId, SagaInstance sagaInstance, Data sagaData, SagaActions<Data> actions, String securityToken, String clientIp) {


        while (true) {

            if (actions.getLocalException().isPresent()) {
                actions = failedActions(actions);

            } else {
                // only do this if successful
                String lastRequestId = null;
                try {
                    lastRequestId = sagaCommandProducer.sendCommands(this.getSagaType(),
                            sagaId,
                            actions.getCommands(),
                            this.makeSagaReplyChannel(), securityToken, clientIp);
                } catch (RuntimeException e) {
                    actions = failedActions(actions);
                }
                sagaInstance.setLastRequestId(lastRequestId);

                updateState(sagaInstance, actions);

                sagaInstance.setSerializedSagaData(SagaDataSerde.serializeSagaData(actions.getUpdatedSagaData().orElse(sagaData)));

                if (actions.isEndState()) {
                    performEndStateActions(sagaId, sagaInstance, actions.isCompensating(), sagaData, securityToken);
                }

                sagaInstanceRepository.save(sagaInstance);

                if (!actions.isLocal())
                    break;

                actions = getStateDefinition().handleReply(actions.getUpdatedState().get(), actions.getUpdatedSagaData().get(), MessageBuilder
                        .withPayload("{}")
                        .withHeader(ReplyMessageHeaders.REPLY_OUTCOME, CommandReplyOutcome.SUCCESS.name())
                        .withHeader(ReplyMessageHeaders.REPLY_TYPE, Success.class.getName())
                        .build(securityToken));
            }
        }
    }

    private void updateState(SagaInstance sagaInstance, SagaActions<Data> actions) {
        actions.getUpdatedState().ifPresent(stateName -> {
            sagaInstance.setStateName(stateName);
            sagaInstance.setEndState(actions.isEndState());
            sagaInstance.setCompensating(actions.isCompensating());
        });
    }


    private Boolean isReplyForThisSagaType(Message message) {
        return message.getHeader(SagaReplyHeaders.REPLY_SAGA_TYPE).map(x -> x.equals(getSagaType())).orElse(false);
    }

    private SagaActions<Data> failedActions(SagaActions<Data> actions) {
        return getStateDefinition().handleReply(actions.getUpdatedState().get(), actions.getUpdatedSagaData().get(), MessageBuilder
                .withPayload("{}")
                .withHeader(ReplyMessageHeaders.REPLY_OUTCOME, CommandReplyOutcome.FAILURE.name())
                .withHeader(ReplyMessageHeaders.REPLY_TYPE, Failure.class.getName())
                .build());
    }

}
