package ir.saga.command;


import ir.saga.common.StashedMessage;
import ir.saga.domain.SagaLock;
import ir.saga.domain.SagaStash;
import ir.saga.message.Message;
import ir.saga.message.MessageBuilder;
import ir.saga.repository.SagaLockRepository;
import ir.saga.repository.SagaStashRepository;
import ir.saga.util.JSonMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class SagaLockManagerImpl implements SagaLockManager {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private final SagaLockRepository sagaLockRepository;
    private final SagaStashRepository sagaStashRepository;


    public SagaLockManagerImpl(SagaLockRepository sagaLockRepository, SagaStashRepository sagaStashRepository) {
        this.sagaLockRepository = sagaLockRepository;
        this.sagaStashRepository = sagaStashRepository;
    }

/*    private String insertIntoSagaLockTableSql;
  private String insertIntoSagaStashTableSql;
  private String selectFromSagaLockTableSql;
  private String selectFromSagaStashTableSql;
  private String updateSagaLockTableSql;
  private String deleteFromSagaLockTableSql;
  private String deleteFromSagaStashTableSql;*/

  /*public SagaLockManagerImpl(EventuateSchema eventuateSchema) {
    String sagaLockTable = eventuateSchema.qualifyTable("saga_lock_table");
    String sagaStashTable = eventuateSchema.qualifyTable("saga_stash_table");

    insertIntoSagaLockTableSql = String.format("INSERT INTO %s(target, saga_type, saga_id) VALUES(?, ?,?)", sagaLockTable);
    insertIntoSagaStashTableSql = String.format("INSERT INTO %s(message_id, target, saga_type, saga_id, message_headers, message_payload) VALUES(?, ?,?, ?, ?, ?)", sagaStashTable);
    selectFromSagaLockTableSql = String.format("select saga_id from %s WHERE target = ? FOR UPDATE", sagaLockTable);
    selectFromSagaStashTableSql = String.format("select message_id, target, saga_type, saga_id, message_headers, message_payload from %s WHERE target = ? ORDER BY message_id LIMIT 1", sagaStashTable);
    updateSagaLockTableSql = String.format("update %s set saga_type = ?, saga_id = ? where target = ?", sagaLockTable);
    deleteFromSagaLockTableSql = String.format("delete from %s where target = ?", sagaLockTable);
    deleteFromSagaStashTableSql = String.format("delete from %s where message_id = ?", sagaStashTable);
  }

  public String getInsertIntoSagaLockTableSql() {
    return insertIntoSagaLockTableSql;
  }

  public String getInsertIntoSagaStashTableSql() {
    return insertIntoSagaStashTableSql;
  }

  public String getSelectFromSagaLockTableSql() {
    return selectFromSagaLockTableSql;
  }

  public String getSelectFromSagaStashTableSql() {
    return selectFromSagaStashTableSql;
  }

  public String getUpdateSagaLockTableSql() {
    return updateSagaLockTableSql;
  }

  public String getDeleteFromSagaLockTableSql() {
    return deleteFromSagaLockTableSql;
  }

  public String getDeleteFromSagaStashTableSql() {
    return deleteFromSagaStashTableSql;
  }*/

    @Override
    public boolean claimLock(String sagaType, String sagaId, String target) {
        while (true)
            try {
                SagaLock sagaLock = new SagaLock();
                sagaLock.setSagaType(sagaType);
                sagaLock.setSagaId(sagaId);
                sagaLock.setTarget(target);
                sagaLockRepository.insert(sagaLock);
                logger.debug("Saga {} {} has locked {}", sagaType, sagaId, target);
                return true;
            } catch (DuplicateKeyException e) {
                Optional<String> owningSagaId = selectForUpdate(target);
                if (owningSagaId.isPresent()) {
                    if (owningSagaId.get().equals(sagaId))
                        return true;
                    else {
                        logger.debug("Saga {} {} is blocked by {} which has locked {}", sagaType, sagaId, owningSagaId, target);
                        return false;
                    }
                }
                logger.debug("{}  is repeating attempt to lock {}", sagaId, target);
            }
    }

    private Optional<String> selectForUpdate(String target) {
        return Optional.ofNullable(sagaLockRepository.findOneByTargetEquals(target).map(SagaLock::getSagaId).orElse(null));
    }

    @Override
    public void stashMessage(String sagaType, String sagaId, String target, Message message) {

        logger.debug("Stashing message from {} for {} : {}", sagaId, target, message);

        SagaStash sagaStash = new SagaStash();
        sagaStash.setMessageId(message.getRequiredHeader(Message.ID));
        sagaStash.setTarget(target);
        sagaStash.setSagaType(sagaType);
        sagaStash.setSagaId(sagaId);
        sagaStash.setMessageHeaders(JSonMapper.toJson(message.getHeaders()));
        sagaStash.setMessagePayload(message.getPayload());

        sagaStashRepository.save(sagaStash);
    }

    @Override
    public Optional<Message> unlock(String sagaId, String target, String securityToken) {
        Optional<String> owningSagaId = selectForUpdate(target);
        Assert.isTrue(owningSagaId.isPresent());
        Assert.isTrue(owningSagaId.get().equals(sagaId), String.format("Expected owner to be %s but is %s", sagaId, owningSagaId.get()));

        logger.debug("Saga {} has unlocked {}", sagaId, target);

        List<StashedMessage> stashedMessages = sagaStashRepository.findAllBySagaIdEqualsAndTargetEquals(sagaId, target).map(sagaStash -> {
            return new StashedMessage(sagaStash.getSagaType(), sagaStash.getSagaId(),
                    MessageBuilder.withPayload(sagaStash.getMessagePayload()).withExtraHeaders("",
                            JSonMapper.fromJson(sagaStash.getMessageHeaders(), Map.class)).build(securityToken));
        }).collect(Collectors.toList());


        if (stashedMessages.isEmpty()) {

            assertEqualToOne(sagaStashRepository.deleteSagaStashByTarget(target));
            return Optional.empty();
        }

        StashedMessage stashedMessage = stashedMessages.get(0);

        logger.debug("unstashed from {}  for {} : {}", sagaId, target, stashedMessage.getMessage());


        assertEqualToOne(sagaLockRepository.update(stashedMessage.getSagaType(), stashedMessage.getSagaId(), target));
        assertEqualToOne(sagaStashRepository.deleteSagaStashByMessageId(stashedMessage.getMessage().getId()));

        return Optional.of(stashedMessage.getMessage());
    }

    private void assertEqualToOne(Long n) {
        if (n != 1)
            throw new RuntimeException("Expected to update one row but updated: " + n);
    }
}
