package net.dongliu.requests;

import net.dongliu.requests.executor.SessionContext;

import javax.annotation.concurrent.ThreadSafe;
import java.net.URL;

/**
 * Http request share cookies etc.
 * This class is thread-safe
 */
@ThreadSafe
public class Session {

    private final SessionContext context;

    Session(SessionContext context) {
        this.context = context;
    }

    /**
     * Start a GET request
     */
    public RequestBuilder get(String url) {
        return newRequest(Methods.GET, url);
    }

    /**
     * Start a GET request
     */
    public RequestBuilder get(URL url) {
        return newRequest(Methods.GET, url);
    }

    /**
     * Start a POST request
     */
    public RequestBuilder post(String url) {
        return newRequest(Methods.POST, url);
    }

    /**
     * Start a POST request
     */
    public RequestBuilder post(URL url) {
        return newRequest(Methods.POST, url);
    }

    /**
     * Start a PUT request
     */
    public RequestBuilder put(String url) {
        return newRequest(Methods.PUT, url);
    }

    /**
     * Start a PUT request
     */
    public RequestBuilder put(URL url) {
        return newRequest(Methods.PUT, url);
    }

    /**
     * Start a HEAD request
     */
    public RequestBuilder head(String url) {
        return newRequest(Methods.HEAD, url);
    }

    /**
     * Start a HEAD request
     */
    public RequestBuilder head(URL url) {
        return newRequest(Methods.HEAD, url);
    }

    /**
     * Start a DELETE request
     */
    public RequestBuilder delete(String url) {
        return newRequest(Methods.DELETE, url);
    }

    /**
     * Start a DELETE request
     */
    public RequestBuilder delete(URL url) {
        return newRequest(Methods.DELETE, url);
    }

    /**
     * Start a PATCH request
     */
    public RequestBuilder patch(String url) {
        return newRequest(Methods.PATCH, url);
    }

    /**
     * Start a PATCH request
     */
    public RequestBuilder patch(URL url) {
        return newRequest(Methods.PATCH, url);
    }

    /**
     * Create new request with method and url
     */
    public RequestBuilder newRequest(String method, String url) {
        return new RequestBuilder().sessionContext(context).url(url).method(method);
    }

    /**
     * Create new request with method and url
     */
    public RequestBuilder newRequest(String method, URL url) {
        return new RequestBuilder().sessionContext(context).url(url).method(method);
    }
}
