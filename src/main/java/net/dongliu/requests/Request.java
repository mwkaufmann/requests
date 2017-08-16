package net.dongliu.requests;

import net.dongliu.requests.body.RequestBody;
import net.dongliu.requests.executor.SessionContext;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.io.Serializable;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Map;

/**
 * The Http request.
 *
 * @author Liu Dong
 */
@Immutable
public class Request implements Serializable {
    private static final long serialVersionUID = -5655150867110223826L;
    private final String method;
    private final Collection<? extends Map.Entry<String, ?>> headers;
    private final Collection<? extends Map.Entry<String, ?>> params;
    private final Collection<? extends Map.Entry<String, ?>> cookies;

    private final String userAgent;
    private final Charset charset;
    @Nullable
    private final RequestBody<?> body;
    private final BasicAuth basicAuth;
    @Nullable
    private final SessionContext sessionContext;
    private final URL url;

    Request(BaseRequestBuilder builder) {
        this.method = builder.method;
        this.headers = builder.headers;
        this.params = builder.params;
        this.cookies = builder.cookies;
        this.userAgent = builder.userAgent;
        this.charset = builder.charset;
        this.body = builder.body;
        this.basicAuth = builder.basicAuth;
        this.sessionContext = builder.sessionContext;
        this.url = builder.url;
    }

    public String getMethod() {
        return method;
    }

    public Collection<? extends Map.Entry<String, ?>> getHeaders() {
        return headers;
    }

    public Collection<? extends Map.Entry<String, ?>> getCookies() {
        return cookies;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public Charset getCharset() {
        return charset;
    }

    @Nullable
    public RequestBody<?> getBody() {
        return body;
    }

    public BasicAuth getBasicAuth() {
        return basicAuth;
    }

    @Nullable
    public SessionContext getSessionContext() {
        return sessionContext;
    }

    public URL getUrl() {
        return url;
    }

    /**
     * Parameter to append to url.
     */
    public Collection<? extends Map.Entry<String, ?>> getParams() {
        return params;
    }

}
