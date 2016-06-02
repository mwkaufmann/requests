package net.dongliu.requests;

import net.dongliu.requests.body.Part;
import net.dongliu.requests.body.RequestBody;

import javax.annotation.Nonnull;
import java.io.InputStream;
import java.net.Proxy;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Http Request builder
 *
 * @author Liu Dong
 */
public final class RequestBuilder {
    String method = "GET";
    String url;
    Collection<Map.Entry<String, String>> headers = Collections.emptyList();
    Collection<Map.Entry<String, String>> cookies = Collections.emptyList();
    String userAgent = "Requests/4.0, Java " + System.getProperty("java.version");
    Collection<Map.Entry<String, String>> params = Collections.emptyList();
    Charset requestCharset = StandardCharsets.UTF_8;
    RequestBody<?> body;
    int socksTimeout = HttpRequest.DEFAULT_TIMEOUT;
    int connectTimeout = HttpRequest.DEFAULT_TIMEOUT;
    Proxy proxy;
    boolean followRedirect = true;
    boolean compress = true;
    boolean verify = true;
    List<CertificateInfo> certs = Collections.emptyList();
    BasicAuth basicAuth;
    @Nonnull
    Session session;

    RequestBuilder(Session session) {
        this.session = Objects.requireNonNull(session);
    }

    public RequestBuilder method(String method) {
        this.method = Objects.requireNonNull(method);
        return this;
    }

    public RequestBuilder url(String url) {
        this.url = Objects.requireNonNull(url);
        return this;
    }

    /**
     * Set request headers.
     */
    public RequestBuilder headers(Collection<? extends Map.Entry<String, ?>> headers) {
        this.headers = Lists.convert(headers, this::toStringPair);
        return this;
    }

    /**
     * Set request headers.
     */
    @SafeVarargs
    public final RequestBuilder headers(Map.Entry<String, ?>... headers) {
        headers(Arrays.asList(headers));
        return this;
    }

    /**
     * Set request headers.
     */
    public final RequestBuilder headers(Map<String, ?> map) {
        this.headers = Lists.convert(map.entrySet(), e -> Parameter.of(e.getKey(), String.valueOf(e.getValue())));
        return this;
    }

    /**
     * Set request cookies.
     */
    public RequestBuilder cookies(Collection<? extends Map.Entry<String, ?>> cookies) {
        this.cookies = Lists.convert(cookies, this::toStringPair);
        return this;
    }

    /**
     * Set request cookies.
     */
    @SafeVarargs
    public final RequestBuilder cookies(Map.Entry<String, ?>... cookies) {
        cookies(Arrays.asList(cookies));
        return this;
    }

    /**
     * Set request cookies.
     */
    public final RequestBuilder cookies(Map<String, ?> map) {
        this.cookies = Lists.convert(map.entrySet(), e -> Parameter.of(e.getKey(), String.valueOf(e.getValue())));
        return this;
    }

    public RequestBuilder userAgent(String userAgent) {
        this.userAgent = Objects.requireNonNull(userAgent);
        return this;
    }

    /**
     * Set url query params.
     */
    public RequestBuilder params(Collection<? extends Map.Entry<String, ?>> params) {
        this.cookies = Lists.convert(params, this::toStringPair);
        return this;
    }

    /**
     * Set url query params.
     */
    @SafeVarargs
    public final RequestBuilder params(Map.Entry<String, ?>... params) {
        this.params = Lists.convert(Arrays.asList(params), this::toStringPair);
        return this;
    }

    /**
     * Set url query params.
     */
    public final RequestBuilder params(Map<String, ?> map) {
        this.params = Lists.convert(map.entrySet(), e -> Parameter.of(e.getKey(), String.valueOf(e.getValue())));
        return this;
    }

    /**
     * Set charset used to encode request params or forms. Default UTF8
     */
    public RequestBuilder requestCharset(Charset charset) {
        requestCharset = charset;
        return this;
    }

    /**
     * Set request body
     */
    public RequestBuilder body(RequestBody<?> body) {
        this.body = body;
        return this;
    }

    /**
     * Set www-form-encoded body. Only for Post
     */
    public RequestBuilder forms(Collection<? extends Map.Entry<String, ?>> params) {
        body = RequestBody.form(Lists.convert(params, this::toStringPair));
        return this;
    }

    /**
     * Set www-form-encoded body. Only for Post
     */
    @SafeVarargs
    public final RequestBuilder forms(Map.Entry<String, ?>... formBody) {
        return forms(Arrays.asList(formBody));
    }

    /**
     * Set www-form-encoded body. Only for Post
     */
    public RequestBuilder forms(Map<String, ?> formBody) {
        return forms(formBody.entrySet());
    }

    /**
     * Set string body
     */
    public RequestBuilder body(String str) {
        body = RequestBody.text(str);
        return this;
    }

    /**
     * Set binary body
     */
    public RequestBuilder body(byte[] bytes) {
        body = RequestBody.bytes(bytes);
        return this;
    }

    /**
     * Set input body
     */
    public RequestBuilder body(InputStream input) {
        body = RequestBody.inputStream(input);
        return this;
    }

    /**
     * For send application/json post request.
     * Must have jackson or gson in classpath, or a runtime exception will be raised
     */
    public RequestBuilder jsonBody(Object value) {
        body = RequestBody.json(value);
        return this;
    }

    /**
     * Set tcp socks timeout in mills
     */
    public RequestBuilder socksTimeout(int timeout) {
        this.socksTimeout = timeout;
        return this;
    }

    /**
     * Set tcp connect timeout in mills
     */
    public RequestBuilder connectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    /**
     * set proxy
     */
    public RequestBuilder proxy(Proxy proxy) {
        this.proxy = proxy;
        return this;
    }

    /**
     * Set auto handle redirect. default true
     */
    public RequestBuilder followRedirect(boolean followRedirect) {
        this.followRedirect = followRedirect;
        return this;
    }

    /**
     * Set accept compressed response. default true
     */
    public RequestBuilder compress(boolean compress) {
        this.compress = compress;
        return this;
    }

    /**
     * Check ssl cert. default true
     */
    public RequestBuilder verify(boolean verify) {
        this.verify = verify;
        return this;
    }

    /**
     * Add trust certs
     */
    public RequestBuilder certs(List<CertificateInfo> certs) {
        this.certs = Objects.requireNonNull(certs);
        return this;
    }

    /**
     * Set http basicAuth by BasicAuth(DigestAuth/NTLMAuth not supported now)
     */
    public RequestBuilder basicAuth(String user, String password) {
        this.basicAuth = new BasicAuth(user, password);
        return this;
    }

    /**
     * Set http basicAuth by BasicAuth(DigestAuth/NTLMAuth not supported now)
     */
    public RequestBuilder basicAuth(BasicAuth basicAuth) {
        this.basicAuth = basicAuth;
        return this;
    }

    HttpRequest build() {
        return new HttpRequest(this);
    }

    /**
     * build http request, and send out
     */
    public RawResponse send() {
        HttpRequest request = build();
        return request.handleRequest();
    }

    //TODO: auto handle datetime header here?
    @SuppressWarnings("unchecked")
    private Map.Entry<String, String> toStringPair(Map.Entry<String, ?> pair) {
        if (pair.getValue() instanceof String) {
            return (Map.Entry<String, String>) pair;
        } else {
            return Parameter.of(pair.getKey(), pair.getValue().toString());
        }
    }

    /**
     * Set connect timeout and socket time out
     */
    public RequestBuilder timeout(int timeout) {
        return connectTimeout(timeout).socksTimeout(timeout);
    }

    /**
     * Set multiPart body. Only form multi-part post
     */
    public final RequestBuilder multiPartBody(Part<?>... parts) {
        return multiPartBody(Arrays.asList(parts));
    }

    /**
     * Set multiPart body. Only form multi-part post
     */
    public final RequestBuilder multiPartBody(Collection<Part<?>> parts) {
        return body(RequestBody.multiPart(parts));
    }
}