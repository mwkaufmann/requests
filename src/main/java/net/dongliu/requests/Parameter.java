package net.dongliu.requests;

import net.dongliu.commons.collection.Pair;

/**
 * Immutable parameter entry, the key and value cannot be null
 *
 * @author Liu Dong
 */
public class Parameter<T> extends Pair<String, T> {
    private static final long serialVersionUID = -6525353427059094141L;

    public Parameter(String key, T value) {
        super(key, value);
    }

    public static <V> Parameter<V> of(String key, V value) {
        return new Parameter<>(key, value);
    }
}
