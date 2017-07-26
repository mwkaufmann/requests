package net.dongliu.requests;

import javax.annotation.concurrent.ThreadSafe;
import java.util.*;

/**
 * Http request share cookies etc.
 * This class is thread-safe
 */
@ThreadSafe
public class Session {

    private final CookieJar cookieJar;

    Session(CookieJar cookieJar) {
        this.cookieJar = cookieJar;
    }

    public RequestBuilder get(String url) {
        return newRequest(Methods.GET, url);
    }

    public RequestBuilder post(String url) {
        return newRequest(Methods.POST, url);
    }

    public RequestBuilder put(String url) {
        return newRequest(Methods.PUT, url);
    }

    public RequestBuilder head(String url) {
        return newRequest(Methods.HEAD, url);
    }

    public RequestBuilder delete(String url) {
        return newRequest(Methods.DELETE, url);
    }

    public RequestBuilder patch(String url) {
        return newRequest(Methods.PATCH, url);
    }

    public RequestBuilder newRequest(String method, String url) {
        return new RequestBuilder(cookieJar).url(url).method(method);
    }
}
