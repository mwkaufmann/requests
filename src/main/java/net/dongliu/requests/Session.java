package net.dongliu.requests;

import net.dongliu.requests.exception.RequestException;
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
    private final Client client;

    Session(Client client) {
        this.client = client;
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
    public BaseRequestBuilder get(String url) throws RequestException {
        return Requests.get(url).session(this).executedBy(client);
    }

    /**
     * head method
     */
    public BaseRequestBuilder head(String url) throws RequestException {
        return Requests.head(url).session(this).executedBy(client);
    }

    /**
     * get url, and return content
     */
    public PostRequestBuilder post(String url) throws RequestException {
        return Requests.post(url).session(this).executedBy(client);
    }

    /**
     * put method
     */
    public BodyRequestBuilder put(String url) throws RequestException {
        return Requests.put(url).session(this).executedBy(client);
    }

    /**
     * delete method
     */
    public BaseRequestBuilder delete(String url) throws RequestException {
        return Requests.delete(url).session(this).executedBy(client);
    }

    /**
     * options method
     */
    public BaseRequestBuilder options(String url) throws RequestException {
        return Requests.options(url).session(this).executedBy(client);
    }

    /**
     * patch method
     */
    public BodyRequestBuilder patch(String url) throws RequestException {
        return Requests.patch(url).session(this).executedBy(client);
    }

    /**
     * trace method
     */
    public BaseRequestBuilder trace(String url) throws RequestException {
        return Requests.trace(url).session(this).executedBy(client);
    }

}
