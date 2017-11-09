package net.dongliu.requests;


import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Collection;
import java.util.List;

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
     * Get all cookies returned by this response
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
     * Get first cookie match the name returned by this response, return null if not found
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
