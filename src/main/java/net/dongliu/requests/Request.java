package net.dongliu.requests;

import net.dongliu.requests.struct.AuthInfo;
import net.dongliu.requests.struct.HttpBody;
import net.dongliu.requests.struct.Method;
import org.apache.http.annotation.Immutable;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Map;

/**
 * Http request
 *
 * @author Dong Liu dongliu@live.cn
 */
@Immutable
class Request {

    private final Method method;
    private final URI url;
    private final Collection<? extends Map.Entry<String, String>> headers;
    private final Collection<? extends Map.Entry<String, String>> cookies;
    private final Collection<? extends Map.Entry<String, String>> parameters;
    private HttpBody httpBody;

    private final AuthInfo authInfo;
    private final Charset charset;

    Request(Method method, URI url, Collection<? extends Map.Entry<String, String>> parameters,
            Collection<? extends Map.Entry<String, String>> headers,
            HttpBody httpBody, Charset charset, AuthInfo authInfo,
            Collection<? extends Map.Entry<String, String>> cookies) {
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

    public Collection<? extends Map.Entry<String, String>> getHeaders() {
        return headers;
    }

    public Collection<? extends Map.Entry<String, String>> getCookies() {
        return cookies;
    }

    public Collection<? extends Map.Entry<String, String>> getParameters() {
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
