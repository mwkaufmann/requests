package net.dongliu.requests;

import net.dongliu.requests.struct.*;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Http request
 *
 * @author Dong Liu dongliu@live.cn
 */
public class Request {

    private final Method method;
    private final URI url;
    private final List<Header> headers;
    private final List<Cookie> cookies;
    private final List<Parameter> parameters;
    private HttpBody httpBody;

    private final AuthInfo authInfo;
    private final Charset charset;

    Request(Method method, URI url, List<Parameter> parameters, List<Header> headers,
            HttpBody httpBody, Charset charset, AuthInfo authInfo, List<Cookie> cookies) {
        this.method = method;
        this.url = url;
        this.parameters = parameters;
        this.httpBody = httpBody;
        this.charset = charset;
        this.headers = headers;
        this.authInfo = authInfo;
        this.cookies = cookies;
    }

    public Method getMethod() {
        return method;
    }

    public URI getUrl() {
        return url;
    }

    public List<Header> getHeaders() {
        return headers;
    }

    public List<Cookie> getCookies() {
        return cookies;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public AuthInfo getAuthInfo() {
        return authInfo;
    }

    public HttpBody getHttpBody() {
        return httpBody;
    }

    public Charset getCharset() {
        return charset;
    }
}
