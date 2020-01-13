package ir.saga.context;

import org.springframework.core.NamedThreadLocal;


public class SagaRequestContextHolder {
    private static final ThreadLocal<SagaRequestAttributes> requestAttributesHolder = new NamedThreadLocal<SagaRequestAttributes>("Saga Request attributes");

    public static void resetRequestAttributes() {
        requestAttributesHolder.remove();
    }

    public static void setRequestAttributes(SagaRequestAttributes attributes) {
        requestAttributesHolder.set(attributes);
    }

    public static SagaRequestAttributes getRequestAttributes() {
        return requestAttributesHolder.get();
    }


}
