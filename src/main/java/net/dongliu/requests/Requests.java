package net.dongliu.requests;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Http request
 *
 * @author Liu Dong
 */
public class Requests {

    private static Logger logger = LoggerFactory.getLogger(Requests.class);

    static {
        // we can modify Host, and other restricted headers
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
    }

    public static RequestBuilder get(String url) {
        return HttpRequest.newBuilder().url(url).method("GET");
    }

    public static RequestBuilder post(String url) {
        return HttpRequest.newBuilder().url(url).method("POST");
    }

    public static RequestBuilder put(String url) {
        return HttpRequest.newBuilder().url(url).method("PUT");
    }

    public static RequestBuilder delete(String url) {
        return HttpRequest.newBuilder().url(url).method("DELETE");
    }

    public static RequestBuilder head(String url) {
        return HttpRequest.newBuilder().url(url).method("HEAD");
    }

    public static RequestBuilder patch(String url) {
        return HttpRequest.newBuilder().url(url).method("PATCH");
    }

    public static Session session() {
        return new Session();
    }
}
