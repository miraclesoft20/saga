package ir.saga.orchestration;

import ir.saga.common.SagaData;
import ir.saga.domain.SagaInstance;

import java.util.Optional;

public interface SagaManager<Data extends SagaData> {
    SagaInstance create(Data sagaData,String currentUsername,String securityToken,String clientIp);

    // TODO or should the saga have a pseudo-step that locks the resource

    SagaInstance create(Data sagaData, Optional<String> lockTarget,String currentUsername,String securityToken,String clientIp);
    SagaInstance create(Data data, Class targetClass, Object targetId,String currentUsername,String securityToken,String clientIp);
}
