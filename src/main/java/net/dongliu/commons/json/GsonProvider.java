package net.dongliu.commons.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonNull;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.stream.JsonWriter;
import net.dongliu.commons.exception.Exceptions;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provider json ability with gson
 *
 * @author Liu Dong
 */
public class GsonProvider implements JsonProvider {
    private static final Logger logger = Logger.getLogger(GsonProvider.class.getName());
    private final Gson gson;

    public GsonProvider() {
        this(getDefaultGson());
    }

    private static Gson getDefaultGson() {
        GsonBuilder gsonBuilder = new GsonBuilder().disableHtmlEscaping();
        registerAllTypeFactories(gsonBuilder);
        return gsonBuilder.create();
    }

    /**
     * Find and register all gson type factory using spi
     */
    private static void registerAllTypeFactories(GsonBuilder gsonBuilder) {
        ServiceLoader<TypeAdapterFactory> loader = ServiceLoader.load(TypeAdapterFactory.class);
        for (TypeAdapterFactory typeFactory : loader) {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("Add gson type factory: " + typeFactory.getClass().getName());
            }
            gsonBuilder.registerTypeAdapterFactory(typeFactory);
        }
    }

    public GsonProvider(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void marshal(Writer writer, Object value) {
        gson.toJson(value, writer);
    }

    @Override
    public void prettyMarshal(Writer writer, @Nullable Object value) {
        if (value == null) {
            gson.toJson(JsonNull.INSTANCE);
            return;
        }
        JsonWriter jsonWriter;
        try {
            jsonWriter = gson.newJsonWriter(writer);
        } catch (IOException e) {
            throw Exceptions.sneakyThrow(e);
        }
        jsonWriter.setIndent("  ");
        gson.toJson(value, value.getClass(), jsonWriter);
    }

    @Override
    public <T> T unmarshal(Reader reader, Type type) {
        return gson.fromJson(reader, type);
    }
}
