package ir.saga.message;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MessageImpl implements Message  {

    private String payload;
    private Map<String, String> headers;

    public MessageImpl() {
    }

    public MessageImpl(String payload, Map<String, String> headers) {
        this.payload = payload;
        this.headers = headers;
    }

   /* @Override
    public String toString() {
        return JSonMapper.toJson(this);
    }
*/
    public String getPayload() {
        return payload;
    }

    @Override
    public Optional<String> getHeader(String name) {
        return Optional.ofNullable(headers.get(name));
    }

    @Override
    public String getRequiredHeader(String name) {
        String s = headers.get(name);
        if (s == null)
            throw new RuntimeException("No such header: " + name + " in this message " + this);
        else
            return s;
    }

    @Override
    public boolean hasHeader(String name) {
        return headers.containsKey(name);
    }

    @Override
    public String getId() {
        return getRequiredHeader(Message.ID);
    }

    @Override
    public String getClientIp() {
        return getHeader(Message.CLIENT_IP).orElse("");
    }

    @Override
    public Map<String, String> getHeaders() {
        return headers;
    }


    @Override
    public void setPayload(String payload) {
        this.payload = payload;
    }

    @Override
    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    @Override
    public void setHeader(String name, String value) {
        if (headers == null)
            headers = new HashMap<>();
        headers.put(name, value);
    }

    @Override
    public void removeHeader(String key) {
        headers.remove(key);
    }

    @Override
    public String getSecurityToken() {
        return  getHeader(Message.SECURITY_TOKEN).orElse("");
    }
}
