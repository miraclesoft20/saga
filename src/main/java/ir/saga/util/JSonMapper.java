package ir.saga.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
/*import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;*/
import ir.saga.message.common.Int128Module;

import java.io.IOException;

public class JSonMapper {
    public static ObjectMapper objectMapper = new ObjectMapper();

    public JSonMapper() {
    }

    public static String toJson(Object x) {
        try {
            return objectMapper.writeValueAsString(x);
        } catch (JsonProcessingException var2) {
            throw new RuntimeException(var2);
        }
    }

    public static <T> T fromJson(String json, Class<T> targetType) {
        try {
            return objectMapper.readValue(json, targetType);
        } catch (IOException var3) {
            throw new RuntimeException(var3);
        }
    }

    public static <T> T fromJsonByName(String json, String targetType) {
        try {
            return (T) objectMapper.readValue(json, JSonMapper.class.getClassLoader().loadClass(targetType));
        } catch (ClassNotFoundException | IOException var3) {
            throw new RuntimeException(var3);
        }
    }

    static {
        objectMapper.configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.registerModule(new Int128Module());
    /*    objectMapper.registerModule((new Jdk8Module()).configureAbsentsAsNulls(true));*/
    }
}


