package ir.saga.error;

public class SagaException extends RuntimeException {
    public SagaException(String errorCode) {
        super(errorCode);
    }
}
