package net.dongliu.requests;

import net.dongliu.requests.body.Part;
import net.dongliu.requests.body.RequestBody;
import net.dongliu.requests.exception.RequestsException;
import net.dongliu.requests.executor.SessionContext;

import javax.annotation.Nullable;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Base Http Request builder.
 *
 * @author Liu Dong
 */
public abstract class BaseRequestBuilder<T extends BaseRequestBuilder<T>> {
    String method = Methods.GET;
    URL url;
    Collection<? extends Map.Entry<String, ?>> headers = Collections.emptyList();
    Collection<? extends Map.Entry<String, ?>> cookies = Collections.emptyList();
    String userAgent = DefaultSettings.USER_AGENT;
    Collection<? extends Map.Entry<String, ?>> params = Collections.emptyList();
    Charset charset = StandardCharsets.UTF_8;
    @Nullable
    RequestBody<?> body;
    BasicAuth basicAuth;
    @Nullable
    SessionContext sessionContext;

    protected BaseRequestBuilder() {
    }

    protected BaseRequestBuilder(Request request) {
        method = request.getMethod();
        headers = request.getHeaders();
        cookies = request.getCookies();
        userAgent = request.getUserAgent();
        charset = request.getCharset();
        body = request.getBody();
        basicAuth = request.getBasicAuth();
        sessionContext = request.getSessionContext();
        this.url = request.getUrl();
        this.params = request.getParams();
    }

    public T method(String method) {
        this.method = Objects.requireNonNull(method);
        return self();
    }

    public T url(String url) {
        try {
            this.url = new URL(Objects.requireNonNull(url));
        } catch (MalformedURLException e) {
            throw new RequestsException("Resolve url error, url: " + url, e);
        }
        return self();
    }

    public T url(URL url) {
        this.url = Objects.requireNonNull(url);
        return self();
    }

    /**
     * Set request headers.
     */
    public T headers(Collection<? extends Map.Entry<String, ?>> headers) {
        this.headers = headers;
        return self();
    }

    /**
     * Set request headers.
     */
    @SafeVarargs
    public final T headers(Map.Entry<String, ?>... headers) {
        headers(Arrays.asList(headers));
        return self();
    }

    /**
     * Set request headers.
     */
    public T headers(Map<String, ?> map) {
        this.headers = map.entrySet();
        return self();
    }

    /**
     * Set request cookies.
     */
    public T cookies(Collection<? extends Map.Entry<String, ?>> cookies) {
        this.cookies = cookies;
        return self();
    }

    /**
     * Set request cookies.
     */
    @SafeVarargs
    public final BaseRequestBuilder cookies(Map.Entry<String, ?>... cookies) {
        cookies(Arrays.asList(cookies));
        return self();
    }

    /**
     * Set request cookies.
     */
    public T cookies(Map<String, ?> map) {
        this.cookies = map.entrySet();
        return self();
    }

    public T userAgent(String userAgent) {
        this.userAgent = Objects.requireNonNull(userAgent);
        return self();
    }

    /**
     * Set url query params.
     */
    public T params(Collection<? extends Map.Entry<String, ?>> params) {
        this.params = params;
        return self();
    }

    /**
     * Set url query params.
     */
    @SafeVarargs
    public final T params(Map.Entry<String, ?>... params) {
        this.params = Arrays.asList(params);
        return self();
    }

    /**
     * Set url query params.
     */
    public T params(Map<String, ?> map) {
        this.params = map.entrySet();
        return self();
    }

    /**
     * Set charset used to encode request params or forms. Default UTF8.
     */
    public T requestCharset(Charset charset) {
        this.charset = charset;
        return self();
    }

    /**
     * Set charset used to encode request params or forms. Default UTF8.
     */
    public T charset(Charset charset) {
        this.charset = charset;
        return self();
    }

    /**
     * Set request body
     */
    public T body(@Nullable RequestBody<?> body) {
        this.body = body;
        return self();
    }

    /**
     * Set www-form-encoded body. Only for Post
     *
     * @deprecated use {@link #body(Collection)} instead
     */
    @Deprecated
    public T forms(Collection<? extends Map.Entry<String, ?>> params) {
        body = RequestBody.form(params);
        return self();
    }

    /**
     * Set www-form-encoded body. Only for Post
     *
     * @deprecated use {@link #body(Map.Entry[])} instead
     */
    @SafeVarargs
    @Deprecated
    public final T forms(Map.Entry<String, ?>... formBody) {
        return forms(Arrays.asList(formBody));
    }

    /**
     * Set www-form-encoded body. Only for Post
     *
     * @deprecated use {@link #body(Map)} instead
     */
    @Deprecated
    public T forms(Map<String, ?> formBody) {
        return forms(formBody.entrySet());
    }


    /**
     * Set www-form-encoded body. Only for Post
     */
    public T body(Collection<? extends Map.Entry<String, ?>> params) {
        body = RequestBody.form(params);
        return self();
    }

    /**
     * Set www-form-encoded body. Only for Post
     */
    @SafeVarargs
    public final T body(Map.Entry<String, ?>... formBody) {
        return body(Arrays.asList(formBody));
    }

    /**
     * Set www-form-encoded body. Only for Post
     */
    public T body(Map<String, ?> formBody) {
        return body(formBody.entrySet());
    }

    /**
     * Set string body
     */
    public T body(String str) {
        body = RequestBody.text(str);
        return self();
    }

    /**
     * Set binary body
     */
    public T body(byte[] bytes) {
        body = RequestBody.bytes(bytes);
        return self();
    }

    /**
     * Set input body
     */
    public T body(InputStream input) {
        body = RequestBody.inputStream(input);
        return self();
    }

    /**
     * For send application/json post request.
     * Must have jackson or gson in classpath, or a runtime exception will be raised
     */
    public T jsonBody(Object value) {
        body = RequestBody.json(value);
        return self();
    }

    private static void checkTimeout(int timeout) {
        if (timeout < 0) {
            throw new IllegalArgumentException("Timeout should not less than 0");
        }
    }

    /**
     * Set http basicAuth by BasicAuth(DigestAuth/NTLMAuth not supported now)
     */
    public T basicAuth(String user, String password) {
        this.basicAuth = new BasicAuth(user, password);
        return self();
    }

    /**
     * Set http basicAuth by BasicAuth(DigestAuth/NTLMAuth not supported now)
     */
    public T basicAuth(BasicAuth basicAuth) {
        this.basicAuth = basicAuth;
        return self();
    }

    /**
     * Builder the request object.
     */
    public Request build() {
        return new Request(this);
    }

    /**
     * Build http request, and send out. This method exists for fluent api
     */
    public abstract RawResponse send();

    /**
     * Set multiPart body. Only form multi-part post.
     *
     * @see #multiPartBody(Collection)
     */
    public T multiPartBody(Part<?>... parts) {
        return multiPartBody(Arrays.asList(parts));
    }

    /**
     * Set multiPart body. Only form multi-part post.
     */
    public T multiPartBody(Collection<Part<?>> parts) {
        return body(RequestBody.multiPart(parts));
    }

    /**
     * Set interceptors
     */
    public T sessionContext(@Nullable SessionContext sessionContext) {
        this.sessionContext = sessionContext;
        return self();
    }

    @SuppressWarnings("unchecked")
    private T self() {
        return (T) this;
    }
}