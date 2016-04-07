package net.dongliu.requests;

import net.dongliu.requests.struct.AuthInfo;
import net.dongliu.requests.struct.HttpBody;
import net.dongliu.requests.struct.Method;
import org.apache.http.annotation.Immutable;

import javax.annotation.Nullable;
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
    @Nullable
    private final Collection<? extends Map.Entry<String, String>> headers;
    @Nullable
    private final Collection<? extends Map.Entry<String, String>> cookies;
    @Nullable
    private final Collection<? extends Map.Entry<String, String>> parameters;
    @Nullable
    private HttpBody httpBody;

    @Nullable
    private final AuthInfo authInfo;
    private final Charset charset;

    Request(Method method, URI url, @Nullable Collection<? extends Map.Entry<String, String>> parameters,
            @Nullable Collection<? extends Map.Entry<String, String>> headers,
            @Nullable HttpBody httpBody, Charset charset, @Nullable AuthInfo authInfo,
            @Nullable Collection<? extends Map.Entry<String, String>> cookies) {
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

    @Nullable
    public Collection<? extends Map.Entry<String, String>> getHeaders() {
        return headers;
    }

    @Nullable
    public Collection<? extends Map.Entry<String, String>> getCookies() {
        return cookies;
    }

    @Nullable
    public Collection<? extends Map.Entry<String, String>> getParameters() {
        return parameters;
    }

    @Nullable
    public AuthInfo getAuthInfo() {
        return authInfo;
    }

    @Nullable
    public HttpBody getHttpBody() {
        return httpBody;
    }

    public Charset getCharset() {
        return charset;
    }
}
