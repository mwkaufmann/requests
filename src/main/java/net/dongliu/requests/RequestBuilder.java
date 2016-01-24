package net.dongliu.requests;

import net.dongliu.requests.exception.RequestException;
import net.dongliu.requests.struct.*;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author Liu Dong
 */
public abstract class RequestBuilder<T extends RequestBuilder<T>> {
    private Client client;
    protected Method method;
    protected URI url;
    protected List<Parameter> parameters;
    protected List<Header> headers;
    protected List<Cookie> cookies;

    protected Charset charset = StandardCharsets.UTF_8;

    //protected CredentialsProvider provider;
    protected AuthInfo authInfo;
    protected Session session;

    /**
     * get http response for return result with Type T.
     */
    <R> Response<R> execute(ResponseProcessor<R> processor) throws RequestException {
        Request request = buildRequest();
        // use custom client
        return client.execute(request, processor, session);
    }

    /**
     * set custom handler to handle http response
     */
    public <R> Response<R> handle(ResponseHandler<R> handler) throws RequestException {
        return execute(new ResponseHandlerAdapter<>(handler));
    }

    /**
     * Get http response for return text result.
     * Decode response body to text with charset provided
     */
    public Response<String> text(Charset responseCharset) throws RequestException {
        return execute(new StringResponseProcessor(responseCharset));
    }

    /**
     * Get http response for return text result.
     * Decode response body to text with charset get from response header
     */
    public Response<String> text() throws RequestException {
        return execute(new StringResponseProcessor(null));
    }

    /**
     * get http response for return byte array result.
     */
    public Response<byte[]> bytes() throws RequestException {
        return execute(ResponseProcessor.bytes);
    }

    /**
     * get http response for write response body to file.
     * only save to file when return status is 200, otherwise return response with null body.
     */
    public Response<File> file(File file) throws RequestException {
        return execute(new FileResponseProcessor(file));
    }

    /**
     * get http response for write response body to file.
     * only save to file when return status is 200, otherwise return response with null body.
     */
    public Response<File> file(String filePath) throws RequestException {
        return execute(new FileResponseProcessor(filePath));
    }

    T client(Client client) {
        this.client = client;
        return self();
    }

    T url(String url) throws RequestException {
        try {
            this.url = new URI(url);
        } catch (URISyntaxException e) {
            throw new RequestException(e);
        }
        return self();
    }

    abstract Request buildRequest();

    /**
     * Set params of url query string. Will overwrite old cookie values
     * This is for set parameters in url query str, If want to set post form params use form((Map&lt;String, ?&gt; params) method
     */
    public T params(Map<String, ?> params) {
        this.parameters = new ArrayList<>(params.size());
        for (Map.Entry<String, ?> entry : params.entrySet()) {
            this.parameters.add(new Parameter(entry.getKey(), entry.getValue()));
        }
        return self();
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
        return self();
    }

    /**
     * Set params of url query string. Will overwrite old param values
     * This is for set parameters in url query str, If want to set post form params use form(Parameter... params) method
     */
    public T params(Collection<Parameter> params) {
        this.parameters = new ArrayList<>(params.size());
        for (Parameter param : params) {
            this.parameters.add(new Parameter(param.getName(), param.getValue()));
        }
        return self();
    }

    /**
     * Add one parameter to url query string. Will overwrite old param values
     * This is for set parameters in url query str, If want to set post form params use form(String key, Object value) method
     */
    public T addParam(String key, Object value) {
        ensureParameters();
        this.parameters.add(new Parameter(key, value));
        return self();
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
        return self();
    }

    /**
     * Set charset used to encode request, default utf-8.
     */
    public T charset(String charset) {
        return charset(Charset.forName(charset));
    }

    T method(Method method) {
        this.method = method;
        return self();
    }

    /**
     * Set headers. Will overwrite old header values
     */
    public T headers(Map<String, ?> params) {
        this.headers = new ArrayList<>();
        for (Map.Entry<String, ?> entry : params.entrySet()) {
            this.headers.add(new Header(entry.getKey(), entry.getValue()));
        }
        return self();
    }

    /**
     * Set headers. Will overwrite old header values
     */
    public T headers(Header... headers) {
        this.headers = new ArrayList<>();
        Collections.addAll(this.headers, headers);
        return self();
    }

    /**
     * Set headers. Will overwrite old header values
     */
    public T headers(List<Header> headers) {
        this.headers = new ArrayList<>();
        for (Header header : headers) {
            this.headers.add(header);
        }
        return self();
    }

    /**
     * Add one header
     */
    public T addHeader(String key, Object value) {
        ensureHeaders();
        this.headers.add(new Header(key, value));
        return self();
    }

    private void ensureHeaders() {
        if (this.headers == null) {
            this.headers = new ArrayList<>();
        }
    }

    /**
     * set http basic auth info
     */
    public T auth(String userName, String password) {
        authInfo = new AuthInfo(userName, password);
        return self();
    }

    /**
     * Set cookies. Will overwrite old cookie values
     */
    public T cookies(Map<String, String> cookies) {
        this.cookies = new ArrayList<>(cookies.size());
        for (Map.Entry<String, String> entry : cookies.entrySet()) {
            this.cookies.add(new Cookie(entry.getKey(), entry.getValue()));
        }
        return self();
    }

    /**
     * Set cookies. Will overwrite old cookie values
     */
    public T cookies(Cookie... cookies) {
        this.cookies = new ArrayList<>(cookies.length);
        Collections.addAll(this.cookies, cookies);
        return self();
    }

    /**
     * Set cookies. Will overwrite old cookie values
     */
    public T cookies(Collection<Cookie> cookies) {
        this.cookies = new ArrayList<>(cookies.size());
        for (Cookie cookie : cookies) {
            this.cookies.add(cookie);
        }
        return self();
    }

    /**
     * Add one cookie
     */
    public T addCookie(String name, String value) {
        ensureCookies();
        this.cookies.add(new Cookie(name, value));
        return self();
    }

    private void ensureCookies() {
        if (this.cookies == null) {
            this.cookies = new ArrayList<>();
        }
    }

    T session(Session session) {
        this.session = session;
        return self();
    }

    protected abstract T self();
}
