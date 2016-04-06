package net.dongliu.requests.struct;

import java.util.Map;
import java.util.Objects;

/**
 * Immutable Name Value pair
 *
 * @author Liu Dong
 */
public class Entry implements Map.Entry<String, String> {
    private final String key;
    private final String value;

    public Entry(String key, String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Create new entry. Parameters cannot be null
     */
    public static Entry of(String key, String value) {
        return new Entry(Objects.requireNonNull(key), Objects.requireNonNull(value));
    }

    /**
     * Create new entry. Parameters cannot be null
     */
    public static Entry of(String key, Object value) {
        return new Entry(Objects.requireNonNull(key), Objects.requireNonNull(value).toString());
    }

    /**
     * Create new entry. Parameters cannot be null
     */
    public static Entry of(String key, int value) {
        return new Entry(Objects.requireNonNull(key), String.valueOf(value));
    }

    /**
     * Create new entry. Parameters cannot be null
     */
    public static Entry of(String key, long value) {
        return new Entry(Objects.requireNonNull(key), String.valueOf(value));
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String setValue(String value) {
        throw new UnsupportedOperationException("Read only");
    }
}
