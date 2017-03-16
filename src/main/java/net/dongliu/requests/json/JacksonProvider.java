package net.dongliu.requests.json;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;

/**
 * Provider json ability via jackson
 *
 * @author Liu Dong
 */
public class JacksonProvider extends AbstractJsonProvider implements JsonProvider {

    private final ObjectMapper objectMapper;

    public JacksonProvider() {
        this(createDefault());
    }

    private static ObjectMapper createDefault() {
        return new ObjectMapper().findAndRegisterModules();
    }

    public JacksonProvider(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void marshal(Writer writer, @Nullable Object value) throws IOException {
        objectMapper.writeValue(writer, value);
    }

    @Override
    public <T> T unmarshal(Reader reader, Type type) throws IOException {
        JavaType javaType = objectMapper.getTypeFactory().constructType(type);
        return objectMapper.readValue(reader, javaType);
    }
}
