package net.dongliu.requests;

import net.dongliu.requests.exception.RequestException;
import net.dongliu.requests.exception.UncheckedURISyntaxException;
import net.dongliu.requests.struct.AuthInfo;
import net.dongliu.requests.struct.Method;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * Http request builder
 *
 * @author Liu Dong
 */
public abstract class RequestBuilder<T extends RequestBuilder<T>> implements IBaseRequestBuilder<T> {
    private PooledClient pooledClient;
    protected Method method;
    protected URI url;
    protected Collection<? extends Map.Entry<String, String>> parameters;
    protected Collection<? extends Map.Entry<String, String>> headers;
    protected Collection<? extends Map.Entry<String, String>> cookies;

    protected Charset charset = StandardCharsets.UTF_8;

    //protected CredentialsProvider provider;
    protected AuthInfo authInfo;
    protected Session session;

    /**
     * Send request and get response
     */
    public RawResponse send() throws RequestException {
        Request request = build();
        return pooledClient.execute(request, session);
    }

    T client(PooledClient pooledClient) {
        this.pooledClient = pooledClient;
        return self();
    }

    T url(String url) throws RequestException {
        try {
            this.url = new URI(url);
        } catch (URISyntaxException e) {
            throw new UncheckedURISyntaxException(e);
        }
        return self();
    }

    public abstract Request build();

    @Override
    public T params(Collection<? extends Map.Entry<String, String>> params) {
        this.parameters = Objects.requireNonNull(params);
        return self();
    }

    @Override
    public T charset(Charset charset) {
        this.charset = charset;
        return self();
    }

    T method(Method method) {
        this.method = method;
        return self();
    }

    @Override
    public T headers(Collection<? extends Map.Entry<String, String>> headers) {
        this.headers = Objects.requireNonNull(headers);
        return self();
    }

    @Override
    public T basicAuth(String userName, String password) {
        authInfo = new AuthInfo(userName, password);
        return self();
    }

    @Override
    public T cookies(Collection<? extends Map.Entry<String, String>> cookies) {
        this.cookies = cookies;
        return self();
    }

    T session(Session session) {
        this.session = session;
        return self();
    }

    protected abstract T self();
}
