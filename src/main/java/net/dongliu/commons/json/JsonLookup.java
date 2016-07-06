package net.dongliu.commons.json;


import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Lookup json, from classpath
 *
 * @author Liu Dong
 */
@ThreadSafe
public class JsonLookup {
    private static final Logger logger = Logger.getLogger(JsonLookup.class.getName());
    private static JsonLookup instance = new JsonLookup();
    @Nullable
    private volatile JsonProvider registeredJsonProvider;

    private JsonLookup() {
    }

    public static JsonLookup getInstance() {
        return instance;
    }

    /**
     * Set json provider for using.
     *
     * @see JsonProvider
     * @see JacksonProvider
     * @see GsonProvider
     */
    public void register(JsonProvider jsonProvider) {
        this.registeredJsonProvider = Objects.requireNonNull(jsonProvider);
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Set json provider to " + jsonProvider.getClass().getName());
        }
    }

    /**
     * If classpath has gson
     */
    boolean hasGson() {
        try {
            Class.forName("com.google.gson.Gson");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * Create Gson Provider
     */
    JsonProvider gsonProvider() {
        return new GsonProvider();
    }

    /**
     * if jackson in classpath
     */
    boolean hasJackson() {
        try {
            Class.forName("com.fasterxml.jackson.databind.ObjectMapper");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * Create Jackson Provider
     */
    JsonProvider jacksonProvider() {
        return new JacksonProvider();
    }

    /**
     * Find one json provider.
     *
     * @throws ProviderNotFoundException if no json provider found
     */
    @Nonnull
    public JsonProvider lookup() {
        JsonProvider jsonProvider = registeredJsonProvider;
        if (jsonProvider == null) {
            jsonProvider = lookedJsonProvider;
        }

        if (jsonProvider != null) {
            return jsonProvider;
        }
        throw new ProviderNotFoundException("Json Provider not found");
    }


    private final JsonProvider lookedJsonProvider = this.lookupInClasspath();

    @Nullable
    private JsonProvider lookupInClasspath() {
        if (hasJackson()) {
            logger.fine("Use default jackson provider to deal with json");
            this.registeredJsonProvider = jacksonProvider();
            return registeredJsonProvider;
        }
        if (hasGson()) {
            logger.fine("Use default gson provider to deal with json");
            registeredJsonProvider = gsonProvider();
            return registeredJsonProvider;
        }
        return null;
    }
}
