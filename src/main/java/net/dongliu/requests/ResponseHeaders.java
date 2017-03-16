package net.dongliu.requests;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * Wrap to deal with response headers
 *
 * @author Liu Dong
 */
class ResponseHeaders {
    private final List<Map.Entry<String, String>> headers;
    private final Map<String, List<String>> map;

    public ResponseHeaders(List<? extends Map.Entry<String, String>> headers) {
        this.headers = Collections.unmodifiableList(Objects.requireNonNull(headers));
        this.map = collectToMap(headers);
    }

    private Map<String, List<String>> collectToMap(List<? extends Map.Entry<String, String>> headers) {
        Map<String, List<String>> map = new HashMap<>();
        for (Map.Entry<String, String> header : headers) {
            String key = header.getKey().toLowerCase();
            String value = header.getValue();
            List<String> list = map.get(key);
            if (list == null) {
                list = new ArrayList<>(4);
                list.add(value);
                map.put(key, list);
            } else {
                list.add(value);
            }
        }
        return map;
    }

    /**
     * Get headers by name. If not exists, return empty list
     */
    @Nonnull
    public List<String> getHeaders(String name) {
        List<String> values = map.get(name.toLowerCase());
        if (values == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(values);
    }

    /**
     * Get the first header value matched name. If not exists, return null
     */
    @Nullable
    public String getFirstHeader(String name) {
        List<String> values = map.get(name.toLowerCase());
        if (values == null) {
            return null;
        }
        return values.get(0);
    }

    /**
     * Get header value as long. If not exists, return defaultValue
     */
    public long getLongHeader(String name, long defaultValue) {
        String firstHeader = getFirstHeader(name);
        if (firstHeader == null) {
            return defaultValue;
        }
        try {
            return Long.parseLong(firstHeader.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    @Nonnull
    public List<Map.Entry<String, String>> getHeaders() {
        return headers;
    }
}
