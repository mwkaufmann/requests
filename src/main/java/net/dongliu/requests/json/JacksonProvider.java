package net.dongliu.requests.json;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.lang.reflect.Type;

/**
 * Provider json ability via jackson
 *
 * @author Liu Dong
 */
public class JacksonProvider implements JsonProvider {

    private final ObjectMapper objectMapper;

    public JacksonProvider() {
        this(new ObjectMapper());
    }

    public JacksonProvider(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void marshal(Writer writer, Object value) {
        try {
            objectMapper.writeValue(writer, value);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public <T> T unmarshal(Reader reader, Type type) {
        JavaType javaType = objectMapper.getTypeFactory().constructType(type);
        try {
            return objectMapper.readValue(reader, javaType);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
