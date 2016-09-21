package net.dongliu.requests;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

/**
 * Immutable parameter entry, the key and value cannot be null
 *
 * @author Liu Dong
 */
public class Parameter<T> implements Map.Entry<String, T>, Serializable {
    private static final long serialVersionUID = -6525353427059094141L;
    private final String key;
    private final T value;

    public Parameter(String key, T value) {
        this.key = Objects.requireNonNull(key);
        this.value = Objects.requireNonNull(value);
    }

    public static <V> Parameter<V> of(String key, V value) {
        return new Parameter<>(key, value);
    }

    @Override
    public String getKey() {
        return key;
    }

    /**
     * Create a new pair with new key
     */
    public Parameter<T> withKey(String newKey) {
        return new Parameter<>(newKey, value);
    }

    public T getValue() {
        return value;
    }

    @Override
    public T setValue(T value) {
        throw new UnsupportedOperationException();
    }

    /**
     * Create a new pair with new value
     */
    public Parameter<T> withValue(T newValue) {
        return new Parameter<>(key, newValue);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Parameter<?> parameter = (Parameter<?>) o;

        if (!key.equals(parameter.key)) return false;
        return value.equals(parameter.value);

    }

    @Override
    public int hashCode() {
        int result = key.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "(" + key + "," + value + ")";
    }


}
