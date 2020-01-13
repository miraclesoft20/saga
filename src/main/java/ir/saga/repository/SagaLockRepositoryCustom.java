package ir.saga.repository;

public interface SagaLockRepositoryCustom {
    Long update(String sagaType, String sagaId, String target);
}
