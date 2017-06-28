package net.dongliu.requests.utils;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * List utils functions.
 * Only for internal use
 *
 * @author Liu Dong
 */
public class ListUtils {

    /**
     * If list is null, return immutable empty list; else return self
     */
    public static <T> List<T> nullToEmpty(@Nullable List<T> list) {
        return list == null ? ListUtils.<T>of() : list;
    }

    /**
     * Create immutable list
     */
    public static <T> List<T> of() {
        return Collections.emptyList();
    }

    /**
     * Create immutable list
     */
    public static <T> List<T> of(T value) {
        return Collections.singletonList(value);
    }

    /**
     * create immutable list
     */
    @SafeVarargs
    public static <T> List<T> of(T... values) {
        return Collections.unmodifiableList(Arrays.asList(values));
    }


}
