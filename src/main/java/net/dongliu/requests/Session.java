package net.dongliu.requests;

import javax.annotation.concurrent.ThreadSafe;
import java.util.*;

/**
 * Http request share cookies etc.
 * This class is thread-safe
 */
@ThreadSafe
public class Session {

    private Set<Cookie> cookies = Collections.emptySet();

    Session() {
    }

    synchronized void updateCookie(Set<Cookie> addCookies) {
        if (addCookies.isEmpty()) {
            return;
        }

        // new cookie will override old cookie with the same (name, domain, path) value, even it is expired
        long now = System.currentTimeMillis();
        Set<Cookie> newCookies = new HashSet<>();
        for (Cookie cookie : cookies) {
            if (!cookie.expired(now) && !addCookies.contains(cookie)) {
                newCookies.add(cookie);
            }
        }
        for (Cookie cookie : addCookies) {
            if (!cookie.expired(now)) {
                newCookies.add(cookie);
            }
        }
        cookies = Collections.unmodifiableSet(newCookies);
    }

    synchronized Set<Cookie> getCookies() {
        return cookies;
    }

    List<Cookie> matchedCookies(String protocol, String domain, String path) {
        long now = System.currentTimeMillis();
        List<Cookie> matched = new ArrayList<>();
        for (Cookie cookie : cookies) {
            if (!cookie.match(protocol, domain, path)) {
                continue;
            }
            if (cookie.expired(now)) {
                continue;
            }
            matched.add(cookie);
        }
        return matched;
    }

    public RequestBuilder get(String url) {
        return new RequestBuilder(this).url(url).method("GET");
    }

    public RequestBuilder post(String url) {
        return new RequestBuilder(this).url(url).method("POST");
    }

    public RequestBuilder put(String url) {
        return new RequestBuilder(this).url(url).method("PUT");
    }

    public RequestBuilder head(String url) {
        return new RequestBuilder(this).url(url).method("HEAD");
    }

    public RequestBuilder delete(String url) {
        return new RequestBuilder(this).url(url).method("DELETE");
    }

    public RequestBuilder patch(String url) {
        return new RequestBuilder(this).url(url).method("PATCH");
    }

    public RequestBuilder newRequest(String method, String url) {
        return new RequestBuilder(this).url(url).method(method);
    }
}
