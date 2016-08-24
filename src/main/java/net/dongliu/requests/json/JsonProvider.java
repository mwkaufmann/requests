package net.dongliu.requests.json;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.lang.reflect.Type;

/**
 * Json provider
 *
 * @author Liu Dong
 */
public interface JsonProvider {

    /**
     * Serialize value to json, and write to writer
     */
    void marshal(Writer writer, @Nullable Object value);

    /**
     * Marshal json to string
     */
    @Nonnull
    default String marshal(Object value) {
        try (StringWriter sw = new StringWriter()) {
            marshal(sw, value);
            return sw.toString();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Deserialize json from reader, with type
     */
    @Nullable
    <T> T unmarshal(Reader reader, Type type);


    /**
     * Deserialize json from reader, with java class
     */
    @Nullable
    default <T> T unmarshal(Reader reader, Class<T> cls) {
        return unmarshal(reader, (Type) cls);
    }

    /**
     * Deserialize json from reader, using type infer
     */
    @Nullable
    default <T> T unmarshal(Reader reader, TypeInfer<T> typeInfer) {
        return unmarshal(reader, typeInfer.getType());
    }

    /**
     * Deserialize json from string, with type
     */
    @Nullable
    default <T> T unmarshal(String str, Type type) {
        try (StringReader reader = new StringReader(str)) {
            return unmarshal(reader, type);
        }
    }

    /**
     * Deserialize json from string, with java class
     */
    @Nullable
    default <T> T unmarshal(String str, Class<T> cls) {
        return unmarshal(str, (Type) cls);
    }

    /**
     * Deserialize json from string, using type infer
     */
    @Nullable
    default <T> T unmarshal(String str, TypeInfer<T> typeInfer) {
        return unmarshal(str, typeInfer.getType());
    }
}
