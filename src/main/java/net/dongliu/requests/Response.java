package net.dongliu.requests;

import jdk.nashorn.internal.ir.annotations.Immutable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Response with transformed result
 *
 * @author Liu Dong
 */
@Immutable
public class Response<T> {
    private final int statusCode;
    private final List<Cookie> cookies;
    private final Headers headers;
    private final T body;

    public Response(int statusCode, List<Cookie> cookies, Headers headers, T body) {
        this.statusCode = statusCode;
        this.cookies = cookies;
        this.headers = headers;
        this.body = body;
    }

    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Get all returned cookies
     */
    @Nonnull
    public Collection<Cookie> getCookies() {
        return cookies;
    }

    /**
     * Get all response headers
     */
    @Nonnull
    public List<Parameter<String>> getHeaders() {
        return headers.getHeaders();
    }


    /**
     * Get first cookie match the name, return null if not exists
     */
    @Nullable
    public Cookie getFirstCookie(String name) {
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) {
                return cookie;
            }
        }
        return null;
    }

    /**
     * Get first header value match the name, return null if not exists
     */
    @Nullable
    public String getFirstHeader(String name) {
        return headers.getFirstHeader(name);
    }

    /**
     * Get all headers values with name. If not exists, return empty list
     */
    @Nonnull
    public List<String> getHeaders(String name) {
        return this.headers.getHeaders(name);
    }

    public T getBody() {
        return body;
    }
}
