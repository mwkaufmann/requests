package net.dongliu.requests.json;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.dongliu.commons.exception.Exceptions;

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
public class JacksonProvider implements JsonProvider {

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
    public void marshal(Writer writer, @Nullable Object value) {
        try {
            objectMapper.writeValue(writer, value);
        } catch (IOException e) {
            throw Exceptions.uncheck(e);
        }
    }

    @Override
    public void prettyMarshal(Writer writer, @Nullable Object value) {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(writer, value);
        } catch (IOException e) {
            throw Exceptions.uncheck(e);
        }
    }

    @Override
    public <T> T unmarshal(Reader reader, Type type) {
        JavaType javaType = objectMapper.getTypeFactory().constructType(type);
        try {
            return objectMapper.readValue(reader, javaType);
        } catch (IOException e) {
            throw Exceptions.uncheck(e);
        }
    }
}
