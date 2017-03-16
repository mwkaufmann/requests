package net.dongliu.requests.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapterFactory;

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
public class GsonProvider extends AbstractJsonProvider implements JsonProvider {
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
    public <T> T unmarshal(Reader reader, Type type) {
        return gson.fromJson(reader, type);
    }
}
