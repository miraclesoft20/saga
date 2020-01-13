package ir.saga.command;

import ir.saga.message.Message;

import java.util.Optional;

public interface SagaLockManager {

  boolean claimLock(String sagaType, String sagaId, String target);

  void stashMessage(String sagaType, String sagaId, String target, Message message);

  Optional<Message> unlock(String sagaId, String target,String securityToken);
}
