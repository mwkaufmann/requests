package net.dongliu.commons.json;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;

/**
 * Convenient methods to use json provider
 *
 * @author Liu Dong
 */
public class Json {
    private static final JsonProvider jsonProvider = JsonLookup.getInstance().lookup();

    public static void marshal(Writer writer, @Nullable Object value) {
        jsonProvider.marshal(writer, value);
    }

    @Nullable
    public static <T> T unmarshal(Reader reader, Type type) {
        return jsonProvider.unmarshal(reader, type);
    }

    @Nullable
    public static <T> T unmarshal(String str, TypeInfer<T> typeInfer) {
        return jsonProvider.unmarshal(str, typeInfer);
    }

    @Nullable
    public static <T> T unmarshal(String str, Type type) {
        return jsonProvider.unmarshal(str, type);
    }

    @Nonnull
    public static String marshal(Object value) {
        return jsonProvider.marshal(value);
    }

    @Nullable
    public static <T> T unmarshal(Reader reader, Class<T> cls) {
        return jsonProvider.unmarshal(reader, cls);
    }

    @Nullable
    public static <T> T unmarshal(Reader reader, TypeInfer<T> typeInfer) {
        return jsonProvider.unmarshal(reader, typeInfer);
    }

    @Nullable
    public static <T> T unmarshal(String str, Class<T> cls) {
        return jsonProvider.unmarshal(str, cls);
    }

    public static void prettyMarshal(Writer writer, @Nullable Object value) {
        jsonProvider.prettyMarshal(writer, value);
    }

    @Nonnull
    public static String prettyMarshal(Object value) {
        return jsonProvider.prettyMarshal(value);
    }
}
