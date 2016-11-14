package net.dongliu.requests;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Http request
 *
 * @author Liu Dong
 */
public class Requests {

    private static Logger logger = LogManager.getLogger();

    static {
        // we can modify Host, and other restricted headers
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
    }

    public static RequestBuilder get(String url) {
        return session().get(url);
    }

    public static RequestBuilder post(String url) {
        return session().post(url);
    }

    public static RequestBuilder put(String url) {
        return session().put(url);
    }

    public static RequestBuilder delete(String url) {
        return session().delete(url);
    }

    public static RequestBuilder head(String url) {
        return session().head(url);
    }

    public static RequestBuilder patch(String url) {
        return session().patch(url);
    }

    /**
     * Create new request with method and url
     */
    public static RequestBuilder newRequest(String method, String url) {
        return session().newRequest(method, url);
    }

    public static Session session() {
        return new Session();
    }
}
