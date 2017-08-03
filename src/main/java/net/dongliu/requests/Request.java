package net.dongliu.requests;

import net.dongliu.requests.body.RequestBody;
import net.dongliu.requests.exception.RequestsException;
import net.dongliu.requests.executor.SessionContext;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Http request
 *
 * @author Liu Dong
 */
@Immutable
public class Request {
    static final int DEFAULT_TIMEOUT = 10_000;

    private final String method;
    private final Collection<? extends Map.Entry<String, ?>> headers;
    private final Collection<? extends Map.Entry<String, ?>> cookies;
    private final String userAgent;
    private final Charset charset;
    @Nullable
    private final RequestBody<?> body;
    private final int socksTimeout;
    private final int connectTimeout;
    @Nullable
    private final Proxy proxy;
    private final boolean followRedirect;
    private final boolean compress;
    private final boolean verify;
    private final List<CertificateInfo> certs;
    private final BasicAuth basicAuth;
    @Nullable
    private final SessionContext sessionContext;
    private final URL url;
    private final boolean keepAlive;

    Request(RequestBuilder builder) {
        method = builder.method;
        headers = builder.headers;
        cookies = builder.cookies;
        userAgent = builder.userAgent;
        charset = builder.requestCharset;
        body = builder.body;
        socksTimeout = builder.socksTimeout;
        connectTimeout = builder.connectTimeout;
        proxy = builder.proxy;
        followRedirect = builder.followRedirect;
        compress = builder.compress;
        verify = builder.verify;
        certs = builder.certs;
        basicAuth = builder.basicAuth;
        sessionContext = builder.sessionContext;
        keepAlive = builder.keepAlive;

        this.url = joinUrl(builder.url, builder.params, charset);
    }

    private static URL joinUrl(String url, Collection<? extends Map.Entry<String, ?>> params,
                               Charset charset) {
        String fullUrl;
        if (params.isEmpty()) {
            fullUrl = url;
        } else {
            fullUrl = url + "?" + URIEncoder.encodeQueries(URIEncoder.toStringParameters(params), charset);
        }
        try {
            return new URL(fullUrl);
        } catch (MalformedURLException e) {
            throw new RequestsException(e);
        }
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

    public int getSocksTimeout() {
        return socksTimeout;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    @Nullable
    public Proxy getProxy() {
        return proxy;
    }

    public boolean isFollowRedirect() {
        return followRedirect;
    }

    public boolean isCompress() {
        return compress;
    }

    public boolean isVerify() {
        return verify;
    }

    public List<CertificateInfo> getCerts() {
        return certs;
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

    public boolean isKeepAlive() {
        return keepAlive;
    }
}
