package ir.saga.repository;

public interface SagaStashRepositoryCustom {
    Long deleteSagaStashByTarget(String target);

    Long deleteSagaStashByMessageId(String messageId);
}
