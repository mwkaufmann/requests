package net.dongliu.requests;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;

/**
 * Parameter.
 * Immutable Map.Entry impl.
 *
 * @author Liu Dong
 */
public class Parameter<V> implements Map.Entry<String, V> {
    private final String key;
    private final V value;

    private Parameter(String key, V value) {
        this.key = Objects.requireNonNull(key);
        this.value = Objects.requireNonNull(value);
    }

    public static <T> Parameter<T> of(String key, T value) {
        return new Parameter<>(key, value);
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public V setValue(V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return key + "=" + value;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Parameter<?> parameter = (Parameter<?>) o;

        if (key != null ? !key.equals(parameter.key) : parameter.key != null) return false;
        return value != null ? value.equals(parameter.value) : parameter.value == null;

    }

    @Override
    public int hashCode() {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}
