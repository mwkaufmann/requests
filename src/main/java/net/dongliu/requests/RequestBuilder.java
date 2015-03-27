package net.dongliu.requests;

import net.dongliu.requests.exception.RuntimeIOException;
import net.dongliu.requests.exception.RuntimeURISyntaxException;
import net.dongliu.requests.struct.*;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * http requests builder
 */
public class RequestBuilder {
    private Method method;
    private URI url;
    private Parameters parameters;
    private String userAgent = "Requests/1.9.0, Java";
    private Headers headers;
    // send cookie values
    private Cookies cookies;

    private Charset charset = StandardCharsets.UTF_8;

    private byte[] body;
    private String strBody;
    // parameter type body(form-encoded)
    private Parameters formParameters;
    // http multi part post request multiParts
    private List<MultiPart> multiParts;
    // http request body from inputStream
    private InputStream in;

    private int connectTimeout = 10_000;
    private int socketTimeout = 10_000;

    private boolean gzip = true;
    // if check ssl certificate
    private boolean verify = true;
    private boolean allowRedirects = true;
    //private CredentialsProvider provider;
    private AuthInfo authInfo;
    private String[] cert;
    private Proxy proxy;

    private Session session;
    private ConnectionPool connectionPool;

    RequestBuilder() {
    }

    /**
     * get http response for return result with Type T.
     */
    <T> Response<T> client(ResponseProcessor<T> processor) throws RuntimeIOException {
        return new RequestExecutor<>(build(), processor, session, connectionPool).execute();
    }

    /**
     * set custom handler to handle http response
     */
    public <T> Response<T> handler(ResponseHandler<T> handler) throws RuntimeIOException {
        return client(new ResponseHandlerAdapter<T>(handler));
    }

    /**
     * Get http response for return text result.
     * Decode response body to text with charset get from response header,
     * use charset(which can be set via charset(Charset charset)) if not exists
     */
    public Response<String> text() throws RuntimeIOException {
        return client(new StringResponseProcessor(charset));
    }

    /**
     * get http response for return byte array result.
     */
    public Response<byte[]> bytes() throws RuntimeIOException {
        return client(ResponseProcessor.bytes);
    }

    /**
     * get http response for write response body to file.
     * only save to file when return status is 200, otherwise return response with null body.
     */
    public Response<File> file(File file) throws RuntimeIOException {
        return client(new FileResponseProcessor(file));
    }

    /**
     * get http response for write response body to file.
     * only save to file when return status is 200, otherwise return response with null body.
     */
    public Response<File> file(String filePath) throws RuntimeIOException {
        return client(new FileResponseProcessor(filePath));
    }

    RequestBuilder url(String url) throws RuntimeURISyntaxException {
        try {
            this.url = new URI(url);
        } catch (URISyntaxException e) {
            throw RuntimeURISyntaxException.of(e);
        }
        return this;
    }

    Request build() {
        return new Request(method, url, parameters, userAgent, headers, in, multiParts, body,
                strBody, formParameters, charset, authInfo, gzip, verify, cookies, allowRedirects,
                connectTimeout, socketTimeout, proxy);
    }

    /**
     * set userAgent
     */
    public RequestBuilder userAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    /**
     * Add params to url query string.
     * This is for set parameters in url query str, If want to set post form params use form((Map&lt;String, ?&gt; params) method
     */
    public RequestBuilder params(Map<String, ?> params) {
        ensureParameters();
        for (Map.Entry<String, ?> entry : params.entrySet()) {
            this.parameters.add(new Parameter(entry.getKey(), entry.getValue()));
        }
        return this;
    }

    /**
     * Add params to url query string.
     * This is for set parameters in url query str, If want to set post form params use form(Parameter... params) method
     */
    public RequestBuilder params(Parameter... params) {
        ensureParameters();
        for (Parameter param : params) {
            this.parameters.add(new Parameter(param.getName(), param.getValue()));
        }
        return this;
    }

    /**
     * Add one parameter to url query string.
     * This is for set parameters in url query str, If want to set post form params use form(String key, Object value) method
     */
    public RequestBuilder param(String key, Object value) {
        ensureParameters();
        this.parameters.add(new Parameter(key, value));
        return this;
    }

    private void ensureParameters() {
        if (this.parameters == null) {
            this.parameters = new Parameters();
        }
    }

    /**
     * Add http form body data, for http post method with form-encoded body.
     */
    public RequestBuilder form(String key, Object value) {
        ensureFormParameters();
        formParameters.add(new Parameter(key, value));
        return this;
    }

    /**
     * Add http form body data, for http post method with form-encoded body.
     */
    public RequestBuilder form(Map<String, ?> params) {
        ensureFormParameters();
        for (Map.Entry<String, ?> e : params.entrySet()) {
            formParameters.add(new Parameter(e.getKey(), e.getValue()));
        }
        return this;
    }

    /**
     * Add http form body data, for http post method with form-encoded body.
     */
    public RequestBuilder form(Parameter... params) {
        ensureFormParameters();
        for (Parameter param : params) {
            formParameters.add(param);
        }
        return this;
    }

    /**
     * Set http form body data, for http post method with form-encoded body.
     * @deprecated use form() method instead
     */
    @Deprecated
    public RequestBuilder data(Map<String, ?> params) {
        return form(params);
    }

    /**
     * Set http form body data, for http post method with form-encoded body.
     *
     * @deprecated use form() method instead
     */
    @Deprecated
    public RequestBuilder data(Parameter... params) {
        return form(params);
    }

    private void ensureFormParameters() {
        if (this.formParameters == null) {
            this.formParameters = new Parameters();
        }
    }

    /**
     * Set http body data for Post/Put request
     *
     * @param data the data to post
     */
    public RequestBuilder data(byte[] data) {
        this.body = data;
        return this;
    }

    /**
     * Set http data from inputStream for Post/Put request
     */
    public RequestBuilder data(InputStream in) {
        this.in = in;
        return this;
    }

    /**
     * Set http data with text.
     * The text string will be encoded, default using utf-8, set charset with charset(Charset charset) method
     */
    public RequestBuilder data(String body) {
        this.strBody = body;
        return this;
    }

    /**
     * Set request charset(and may be used for response body decode), default utf-8
     */
    public RequestBuilder charset(Charset charset) {
        this.charset = charset;
        return this;
    }

    /**
     * Set request charset(and may be used for response body decode), default "UTF-8"
     */
    public RequestBuilder charset(String charset) {
        this.charset = Charset.forName(charset);
        return this;
    }

    RequestBuilder method(Method method) {
        this.method = method;
        return this;
    }

    /**
     * add headers
     */
    public RequestBuilder headers(Map<String, ?> params) {
        ensureHeaders();
        for (Map.Entry<String, ?> entry : params.entrySet()) {
            this.headers.add(new Header(entry.getKey(), entry.getValue()));
        }
        return this;
    }

    /**
     * add headers
     */
    public RequestBuilder headers(Header... headers) {
        ensureHeaders();
        for (Header header : headers) {
            this.headers.add(header);
        }
        return this;
    }

    /**
     * add one header
     */
    public RequestBuilder header(String key, Object value) {
        ensureHeaders();
        this.headers.add(new Header(key, value));
        return this;
    }

    private void ensureHeaders() {
        if (this.headers == null) {
            this.headers = new Headers();
        }
    }

    /**
     * set socket connect and read timeout in milliseconds. default is 10_000.
     * A timeout value of zero is interpreted as an infinite timeout.
     * A negative value is interpreted as undefined (system default).
     */
    public RequestBuilder timeout(int timeout) {
        this.socketTimeout = this.connectTimeout = timeout;
        return this;
    }

    /**
     * set socket connect and read timeout in milliseconds. default is 10_000.
     * A timeout value of zero is interpreted as an infinite timeout.
     * A negative value is interpreted as undefined (system default).
     */
    public RequestBuilder timeout(int connectTimeout, int socketTimeout) {
        this.connectTimeout = connectTimeout;
        this.socketTimeout = socketTimeout;
        return this;
    }

    /**
     * set proxy
     */
    public RequestBuilder proxy(Proxy proxy) throws RuntimeURISyntaxException {
        this.proxy = proxy;
        return this;
    }

    /**
     * if send gzip requests. default true
     */
    public RequestBuilder gzip(boolean gzip) {
        this.gzip = gzip;
        return this;
    }

    /**
     * set false to disable ssl check for https requests
     */
    public RequestBuilder verify(boolean verify) {
        this.verify = verify;
        return this;
    }

    /**
     * set http basic auth info
     */
    public RequestBuilder auth(String userName, String password) {
        authInfo = new AuthInfo(userName, password);
        return this;
    }

    /**
     * add cookies
     */
    public RequestBuilder cookies(Map<String, String> cookies) {
        ensureCookies();
        for (Map.Entry<String, String> entry : cookies.entrySet()) {
            this.cookies.add(new Cookie(entry.getKey(), entry.getValue()));
        }
        return this;
    }

    /**
     * add cookies
     */
    public RequestBuilder cookies(Cookie... cookies) {
        ensureCookies();
        for (Cookie cookie : cookies) {
            this.cookies.add(cookie);
        }
        return this;
    }

    /**
     * add cookie
     */
    public RequestBuilder cookie(String name, String value) {
        ensureCookies();
        this.cookies.add(new Cookie(name, value));
        return this;
    }

    private void ensureCookies() {
        if (this.cookies == null) {
            this.cookies = new Cookies();
        }
    }

    /**
     * if follow redirect
     */
    public RequestBuilder allowRedirects(boolean allowRedirects) {
        this.allowRedirects = allowRedirects;
        return this;
    }

    /**
     * set cert path
     * TODO: custom cert
     */
    public RequestBuilder cert(String... cert) {
        throw new UnsupportedOperationException();
//        this.cert = cert;
//        return this;
    }

    /**
     * add multi part file, will send multiPart requests.
     *
     * @param name the http request field name for this file
     * @param file the file to be send
     */
    public RequestBuilder multiPart(String name, File file) {
        ensureMultiPart();
        this.multiParts.add(new MultiPart(name, file));
        return this;
    }

    /**
     * add multi part file, will send multiPart requests.
     * this should be used with post method
     *
     * @param name     the http request field name for this file
     * @param mimeType the mimeType for file
     * @param file     the file to be send
     */
    public RequestBuilder multiPart(String name, String mimeType, File file) {
        ensureMultiPart();
        this.multiParts.add(new MultiPart(name, mimeType, file));
        return this;
    }

    /**
     * add multi part field by byte array, will send multiPart requests.
     *
     * @param name  the http request field name for this field
     * @param bytes the multipart request content
     */
    public RequestBuilder multiPart(String name, String mimeType, byte[] bytes) {
        multiPart(name, mimeType, null, bytes);
        return this;
    }

    /**
     * add multi part field by byte array, will send multiPart requests.
     *
     * @param name     the http request field name for this field
     * @param fileName the file name. can be null
     * @param bytes    the multipart request content
     */
    public RequestBuilder multiPart(String name, String mimeType, String fileName, byte[] bytes) {
        ensureMultiPart();
        this.multiParts.add(new MultiPart(name, mimeType, fileName, bytes));
        return this;
    }

    /**
     * add multi part field by inputStream, will send multiPart requests.
     *
     * @param name     the http request field name for this field
     * @param mimeType the mimeType for file
     * @param in       the inputStream for the content
     */
    public RequestBuilder multiPart(String name, String mimeType, InputStream in) {
        multiPart(name, mimeType, null, in);
        return this;
    }

    /**
     * add multi part field by inputStream, will send multiPart requests.
     *
     * @param name     the http request field name for this field
     * @param mimeType the mimeType for file
     * @param fileName the file name. can be null
     * @param in       the inputStream for the content
     */
    public RequestBuilder multiPart(String name, String mimeType, String fileName, InputStream in) {
        ensureMultiPart();
        this.multiParts.add(new MultiPart(name, mimeType, fileName, in));
        return this;
    }

    /**
     * Add multi part key-value parameter.
     *
     * @param name  the http request field name
     * @param value the file to be send
     */
    public RequestBuilder multiPart(String name, String value) {
        ensureMultiPart();
        this.multiParts.add(new MultiPart(name, value));
        return this;
    }

    private void ensureMultiPart() {
        if (this.multiParts == null) {
            this.multiParts = new ArrayList<>();
        }
    }

    RequestBuilder session(Session session) {
        this.session = session;
        return this;
    }

    /**
     * set connection pool. used to reuse http connections.
     */
    public RequestBuilder connectionPool(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
        return this;
    }
}
