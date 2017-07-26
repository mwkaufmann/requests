package net.dongliu.requests;


/**
 * Http request
 *
 * @author Liu Dong
 */
public class Requests {

    static {
        // we can modify Host, and other restricted headers
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
    }

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
        return new RequestBuilder(NopCookieJar.instance).method(method).url(url);
    }

    public static Session session() {
        return new Session(new DefaultCookieJar());
    }
}
