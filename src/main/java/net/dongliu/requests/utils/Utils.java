package net.dongliu.requests.utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Only for internal use.
 * Commons lang method.
 */
public class Utils {

    @Nonnull
    public static <T> T ifNullThen(@Nullable T value, @Nonnull T defaultValue) {
        return value != null ? value : defaultValue;
    }
}
