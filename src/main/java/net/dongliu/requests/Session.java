package net.dongliu.requests;

import net.dongliu.requests.exception.RequestException;
import net.dongliu.requests.struct.Method;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;

/**
 * one http session, share cookies, basic auth across http request.
 *
 * @author Dong Liu dongliu@live.cn
 */
public class Session {
    private final HttpClientContext context;
    // null if do not set connectionPool
    private final PooledClient pooledClient;

    Session(PooledClient pooledClient) {
        this.pooledClient = pooledClient;
        context = HttpClientContext.create();
        BasicCookieStore cookieStore = new BasicCookieStore();
        context.setCookieStore(cookieStore);
    }

    HttpClientContext getContext() {
        return context;
    }

    /**
     * get method
     */
    public HeadOnlyRequestBuilder get(String url) throws RequestException {
        return new HeadOnlyRequestBuilder().client(pooledClient).session(this).method(Method.GET).url(url);
    }

    /**
     * head method
     */
    public HeadOnlyRequestBuilder head(String url) throws RequestException {
        return new HeadOnlyRequestBuilder().client(pooledClient).session(this).method(Method.HEAD).url(url);
    }

    /**
     * get url, and return content
     */
    public PostRequestBuilder post(String url) throws RequestException {
        return new PostRequestBuilder().client(pooledClient).session(this).method(Method.POST).url(url);
    }

    /**
     * put method
     */
    public BodyRequestBuilder put(String url) throws RequestException {
        return new BodyRequestBuilder().client(pooledClient).session(this).method(Method.PUT).url(url);
    }

    /**
     * delete method
     */
    public HeadOnlyRequestBuilder delete(String url) throws RequestException {
        return new HeadOnlyRequestBuilder().client(pooledClient).session(this).method(Method.DELETE).url(url);
    }

    /**
     * options method
     */
    public HeadOnlyRequestBuilder options(String url) throws RequestException {
        return new HeadOnlyRequestBuilder().client(pooledClient).session(this).method(Method.OPTIONS).url(url);
    }

    /**
     * patch method
     */
    public BodyRequestBuilder patch(String url) throws RequestException {
        return new BodyRequestBuilder().client(pooledClient).session(this).method(Method.PATCH).url(url);
    }

    /**
     * trace method
     */
    public HeadOnlyRequestBuilder trace(String url) throws RequestException {
        return new HeadOnlyRequestBuilder().client(pooledClient).session(this).method(Method.TRACE).url(url);
    }

}
