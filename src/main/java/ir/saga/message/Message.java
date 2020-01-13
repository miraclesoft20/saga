package ir.saga.message;

import java.io.Serializable;
import java.util.Map;
import java.util.Optional;

public interface Message extends Serializable {
    String ID = "ID";
    String PARTITION_ID = "PARTITION_ID";
    String DESTINATION = "DESTINATION";
    String DATE = "DATE";
    String SECURITY_TOKEN = "SECURITY_TOKEN";
    String CLIENT_IP = "CLIENT_IP";

    String getId();

    Map<String, String> getHeaders();

    String getPayload();

    Optional<String> getHeader(String name);

    String getRequiredHeader(String name);

    String getSecurityToken();

    String getClientIp();

    boolean hasHeader(String name);

    void setPayload(String payload);

    void setHeaders(Map<String, String> headers);

    void setHeader(String name, String value);

    void removeHeader(String key);
}
