package net.dongliu.requests;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * Common parent for RawResponse and Response
 */
class AbstractResponse {
    protected final int statusCode;
    protected final List<Cookie> cookies;
    protected final Headers headers;

    protected AbstractResponse(int statusCode, List<Cookie> cookies, Headers headers) {
        this.statusCode = statusCode;
        this.cookies = Collections.unmodifiableList(cookies);
        this.headers = headers;
    }

    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Get all cookies returned by this response
     */
    @Nonnull
    public List<Cookie> getCookies() {
        return cookies;
    }

    /**
     * Get all response headers
     */
    @Nonnull
    public List<Header> getHeaders() {
        return headers.getHeaders();
    }


    /**
     * Get first cookie match the name returned by this response, return null if not found
     *
     * @deprecated using {{@link #getCookie(String)}} instead
     */
    @Deprecated
    @Nullable
    public Cookie getFirstCookie(String name) {
        return getCookie(name);
    }

    /**
     * Get first cookie match the name returned by this response, return null if not found
     */
    @Nullable
    public Cookie getCookie(String name) {
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) {
                return cookie;
            }
        }
        return null;
    }

    /**
     * Get first header value match the name, return null if not exists
     *
     * @deprecated using {@link #getHeader(String)} instead
     */
    @Deprecated
    @Nullable
    public String getFirstHeader(String name) {
        return headers.getFirstHeader(name);
    }

    /**
     * Get first header value match the name, return null if not exists
     */
    @Nullable
    public String getHeader(String name) {
        return headers.getHeader(name);
    }

    /**
     * Get all headers values with name. If not exists, return empty list
     */
    @Nonnull
    public List<String> getHeaders(String name) {
        return this.headers.getHeaders(name);
    }

}
