package net.dongliu.requests;

import jdk.nashorn.internal.ir.annotations.Immutable;

import java.util.Set;

/**
 * Response with transformed result
 *
 * @author Liu Dong
 */
@Immutable
public class Response<T> {
    private final int statusCode;
    private final Set<Cookie> cookies;
    private final ResponseHeaders headers;
    private final T body;

    public Response(int statusCode, Set<Cookie> cookies, ResponseHeaders headers, T body) {
        this.statusCode = statusCode;
        this.cookies = cookies;
        this.headers = headers;
        this.body = body;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Set<Cookie> getCookies() {
        return cookies;
    }

    public ResponseHeaders getHeaders() {
        return headers;
    }

    public T getBody() {
        return body;
    }
}
