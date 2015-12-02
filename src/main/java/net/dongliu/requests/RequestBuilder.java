package net.dongliu.requests;

import net.dongliu.requests.exception.RequestException;
import net.dongliu.requests.struct.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Liu Dong
 */
public abstract class RequestBuilder<T extends RequestBuilder<T>> {
    protected Method method;
    protected URI url;
    protected List<Parameter> parameters;
    protected String userAgent = Utils.defaultUserAgent;
    protected List<Header> headers;
    protected List<Cookie> cookies;

    protected Charset charset = StandardCharsets.UTF_8;

    protected int connectTimeout = 10_000;
    protected int socketTimeout = 10_000;

    protected boolean gzip = true;
    // if check ssl certificate
    protected boolean verify = true;
    protected boolean allowRedirects = true;
    protected boolean allowPostRedirects = false;
    //protected CredentialsProvider provider;
    protected AuthInfo authInfo;
    protected String[] cert;
    protected Proxy proxy;

    protected Session session;
    protected PooledClient pooledClient;

    /**
     * get http response for return result with Type T.
     */
    <R> Response<R> client(ResponseProcessor<R> processor) throws RequestException {
        try {
            return new RequestExecutor<>(build(), processor, session, pooledClient).execute();
        } catch (IOException e) {
            throw new RequestException(e);
        }
    }

    /**
     * set custom handler to handle http response
     */
    public <R> Response<R> handler(ResponseHandler<R> handler) throws RequestException {
        return client(new ResponseHandlerAdapter<R>(handler));
    }

    /**
     * Get http response for return text result.
     * Decode response body to text with charset provided
     */
    public Response<String> text(Charset responseCharset) throws RequestException {
        return client(new StringResponseProcessor(responseCharset));
    }

    /**
     * Get http response for return text result.
     * Decode response body to text with charset get from response header
     */
    public Response<String> text() throws RequestException {
        return client(new StringResponseProcessor(null));
    }

    /**
     * get http response for return byte array result.
     */
    public Response<byte[]> bytes() throws RequestException {
        return client(ResponseProcessor.bytes);
    }

    /**
     * get http response for write response body to file.
     * only save to file when return status is 200, otherwise return response with null body.
     */
    public Response<File> file(File file) throws RequestException {
        return client(new FileResponseProcessor(file));
    }

    /**
     * get http response for write response body to file.
     * only save to file when return status is 200, otherwise return response with null body.
     */
    public Response<File> file(String filePath) throws RequestException {
        return client(new FileResponseProcessor(filePath));
    }

    T url(String url) throws RequestException {
        try {
            this.url = new URI(url);
        } catch (URISyntaxException e) {
            throw new RequestException(e);
        }
        return (T) this;
    }

    abstract Request build();

    /**
     * set userAgent
     */
    public T userAgent(String userAgent) {
        Objects.requireNonNull(userAgent);
        this.userAgent = userAgent;
        return (T) this;
    }

    /**
     * Set params of url query string. Will overwrite old cookie values
     * This is for set parameters in url query str, If want to set post form params use form((Map&lt;String, ?&gt; params) method
     */
    public T params(Map<String, ?> params) {
        this.parameters = new ArrayList<>(params.size());
        for (Map.Entry<String, ?> entry : params.entrySet()) {
            this.parameters.add(new Parameter(entry.getKey(), entry.getValue()));
        }
        return (T) this;
    }

    /**
     * Set params of url query string. Will overwrite old param values
     * This is for set parameters in url query str, If want to set post form params use form(Parameter... params) method
     */
    public T params(Parameter... params) {
        this.parameters = new ArrayList<>(params.length);
        for (Parameter param : params) {
            this.parameters.add(new Parameter(param.getName(), param.getValue()));
        }
        return (T) this;
    }

    /**
     * Add params of url query string.
     * This is for add parameters in url query str, If want to set post form params use form((Map&lt;String, ?&gt; params) method
     */
    public T addParams(Map<String, ?> params) {
        ensureParameters();
        for (Map.Entry<String, ?> entry : params.entrySet()) {
            this.parameters.add(new Parameter(entry.getKey(), entry.getValue()));
        }
        return (T) this;
    }

    /**
     * Add params of url query string.
     * This is for add parameters in url query str, If want to set post form params use form(Parameter... params) method
     */
    public T addParams(Parameter... params) {
        ensureParameters();
        for (Parameter param : params) {
            this.parameters.add(new Parameter(param.getName(), param.getValue()));
        }
        return (T) this;
    }

    /**
     * Add one parameter to url query string. Will overwrite old param values
     * This is for set parameters in url query str, If want to set post form params use form(String key, Object value) method
     */
    public T addParam(String key, Object value) {
        ensureParameters();
        this.parameters.add(new Parameter(key, value));
        return (T) this;
    }

    private void ensureParameters() {
        if (this.parameters == null) {
            this.parameters = new ArrayList<>();
        }
    }

    /**
     * Set charset used to encode request, default utf-8.
     */
    public T charset(Charset charset) {
        this.charset = charset;
        return (T) this;
    }

    /**
     * Set charset used to encode request, default utf-8.
     */
    public T charset(String charset) {
        return charset(Charset.forName(charset));
    }

    T method(Method method) {
        this.method = method;
        return (T) this;
    }

    /**
     * Set headers. Will overwrite old header values
     */
    public T headers(Map<String, ?> params) {
        this.headers = new ArrayList<>();
        for (Map.Entry<String, ?> entry : params.entrySet()) {
            this.headers.add(new Header(entry.getKey(), entry.getValue()));
        }
        return (T) this;
    }

    /**
     * Set headers. Will overwrite old header values
     */
    public T headers(Header... headers) {
        this.headers = new ArrayList<>();
        for (Header header : headers) {
            this.headers.add(header);
        }
        return (T) this;
    }

    /**
     * Add headers.
     */
    public T addHeaders(Map<String, ?> params) {
        ensureHeaders();
        for (Map.Entry<String, ?> entry : params.entrySet()) {
            this.headers.add(new Header(entry.getKey(), entry.getValue()));
        }
        return (T) this;
    }

    /**
     * Add headers.
     */
    public T addHeaders(Header... headers) {
        ensureHeaders();
        for (Header header : headers) {
            this.headers.add(header);
        }
        return (T) this;
    }

    /**
     * Add one header
     */
    public T addHeader(String key, Object value) {
        ensureHeaders();
        this.headers.add(new Header(key, value));
        return (T) this;
    }

    private void ensureHeaders() {
        if (this.headers == null) {
            this.headers = new ArrayList<>();
        }
    }

    /**
     * set socket connect and read timeout in milliseconds. default is 10_000.
     * A timeout value of zero is interpreted as an infinite timeout.
     * A negative value is interpreted as undefined (system default).
     */
    public T timeout(int timeout) {
        this.socketTimeout = this.connectTimeout = timeout;
        return (T) this;
    }

    /**
     * set socket connect and read timeout in milliseconds. default is 10_000.
     * A timeout value of zero is interpreted as an infinite timeout.
     * A negative value is interpreted as undefined (system default).
     */
    public T timeout(int connectTimeout, int socketTimeout) {
        this.connectTimeout = connectTimeout;
        this.socketTimeout = socketTimeout;
        return (T) this;
    }

    /**
     * set proxy
     */
    public T proxy(Proxy proxy) {
        this.proxy = proxy;
        return (T) this;
    }

    /**
     * if send gzip requests. default true
     */
    public T gzip(boolean gzip) {
        this.gzip = gzip;
        return (T) this;
    }

    /**
     * set false to disable ssl check for https requests
     */
    public T verify(boolean verify) {
        this.verify = verify;
        return (T) this;
    }

    /**
     * set http basic auth info
     */
    public T auth(String userName, String password) {
        authInfo = new AuthInfo(userName, password);
        return (T) this;
    }

    /**
     * Set cookies. Will overwrite old cookie values
     */
    public T cookies(Map<String, String> cookies) {
        this.cookies = new ArrayList<>(cookies.size());
        for (Map.Entry<String, String> entry : cookies.entrySet()) {
            this.cookies.add(new Cookie(entry.getKey(), entry.getValue()));
        }
        return (T) this;
    }

    /**
     * Set cookies. Will overwrite old cookie values
     */
    public T cookies(Cookie... cookies) {
        this.cookies = new ArrayList<>(cookies.length);
        for (Cookie cookie : cookies) {
            this.cookies.add(cookie);
        }
        return (T) this;
    }


    /**
     * Add cookies.
     */
    public T AddCookies(Map<String, String> cookies) {
        ensureCookies();
        for (Map.Entry<String, String> entry : cookies.entrySet()) {
            this.cookies.add(new Cookie(entry.getKey(), entry.getValue()));
        }
        return (T) this;
    }

    /**
     * Add cookies.
     */
    public T AddCookies(Cookie... cookies) {
        ensureCookies();
        for (Cookie cookie : cookies) {
            this.cookies.add(cookie);
        }
        return (T) this;
    }

    /**
     * Add one cookie
     */
    public T addCookie(String name, String value) {
        ensureCookies();
        this.cookies.add(new Cookie(name, value));
        return (T) this;
    }

    private void ensureCookies() {
        if (this.cookies == null) {
            this.cookies = new ArrayList<>();
        }
    }

    /**
     * If follow get/head redirect, default true.
     * This method not set following redirect for post/put/delete method, use {@code allowPostRedirects} if you want this
     */
    public T allowRedirects(boolean allowRedirects) {
        this.allowRedirects = allowRedirects;
        return (T) this;
    }

    /**
     * If follow POST/PUT/DELETE redirect, default false. This method work for post method.
     */
    public T allowPostRedirects(boolean allowPostRedirects) {
        this.allowPostRedirects = allowPostRedirects;
        return (T) this;
    }

    /**
     * set cert path
     * TODO: custom cert
     */
    public T cert(String... cert) {
        throw new UnsupportedOperationException();
//        this.cert = cert;
//        return this;
    }


    T session(Session session) {
        this.session = session;
        return (T) this;
    }

    /**
     * Set connection pool. used to reuse http connections.
     */
    T executedBy(PooledClient pooledClient) {
        this.pooledClient = pooledClient;
        return (T) this;
    }
}
