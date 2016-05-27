package net.dongliu.requests.json;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Lookup json, from classpath
 *
 * @author Liu Dong
 */
public class JsonLookup {

    private static Logger logger = LoggerFactory.getLogger(JsonLookup.class);
    private static JsonLookup jsonLookup = new JsonLookup();

    @Nullable
    private JsonProvider jsonProvider;
    private boolean jsonlooked;

    private JsonLookup() {
    }

    public static JsonLookup getInstance() {
        return jsonLookup;
    }

    /**
     * Set json provider for Requests.
     */
    public synchronized void setJsonProvider(@Nonnull JsonProvider jsonProvider) {
        this.jsonProvider = Objects.requireNonNull(jsonProvider);
        logger.info("Set json provider to {}", jsonProvider.getClass().getName());
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
     * Find one json provider. if none, return null
     */
    @Nonnull
    public synchronized JsonProvider lookup() {
        if (jsonProvider != null) {
            return jsonProvider;
        }
        if (hasJackson()) {
            logger.info("Use default jackson provider to deal with json");
            this.jsonProvider = jacksonProvider();
            return jsonProvider;
        }
        if (hasGson()) {
            logger.info("Use default gson provider to deal with json");
            jsonProvider = gsonProvider();
            return jsonProvider;
        }
        throw new RuntimeException("No json provider found");
    }
}
