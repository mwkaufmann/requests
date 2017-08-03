package net.dongliu.requests;


import net.dongliu.requests.executor.RequestProvider;
import net.dongliu.requests.executor.RequestProviders;

/**
 * Http request utils methods.
 *
 * @author Liu Dong
 */
public class Requests {

    private static RequestProvider provider = RequestProviders.lookup();

    public static RequestBuilder get(String url) {
        return newRequest(Methods.GET, url);
    }

    public static RequestBuilder post(String url) {
        return newRequest(Methods.POST, url);
    }

    public static RequestBuilder put(String url) {
        return newRequest(Methods.PUT, url);
    }

    public static RequestBuilder delete(String url) {
        return newRequest(Methods.DELETE, url);
    }

    public static RequestBuilder head(String url) {
        return newRequest(Methods.HEAD, url);
    }

    public static RequestBuilder patch(String url) {
        return newRequest(Methods.PATCH, url);
    }

    /**
     * Create new request with method and url
     */
    public static RequestBuilder newRequest(String method, String url) {
        return new RequestBuilder().method(method).url(url);
    }

    /**
     * Create new session.
     */
    public static Session session() {
        return new Session(provider.newSessionContext());
    }
}
