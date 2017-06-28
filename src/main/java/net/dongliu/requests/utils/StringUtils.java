package net.dongliu.requests.utils;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Only for internal use
 */
public class StringUtils {

    public static String appendSuffixIfNotExists(String str, String suffix) {
        if (!str.endsWith(suffix)) {
            str += suffix;
        }
        return str;
    }

    public static String firstNonNull(@Nullable String str, String defaultStr) {
        if (str != null) {
            return str;
        }
        return Objects.requireNonNull(defaultStr);
    }
}
