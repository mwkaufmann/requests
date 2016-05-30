package net.dongliu.requests;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Http request share cookies, basic auths(?)
 * This class is thread-safe
 */
public class Session {

    private final AtomicReference<Set<Cookie>> cookies = new AtomicReference<>(Collections.emptySet());

    Session() {
    }

    static Session create(Set<Cookie> cookies) {
        Session session = new Session();
        session.updateCookie(cookies);
        return session;
    }

    void updateCookie(Set<Cookie> newCookies) {
        if (newCookies.isEmpty()) {
            return;
        }

        boolean success;
        do {
            Set<Cookie> oldCookies = cookies.get();
            Set<Cookie> s = Utils.mergeCookie(oldCookies, newCookies);
            success = cookies.compareAndSet(oldCookies, s);
        } while (!success);

    }

    Set<Cookie> getCookies() {
        return cookies.get();
    }

    public RequestBuilder get(String url) {
        return Requests.get(url).session(this);
    }

    public RequestBuilder post(String url) {
        return Requests.post(url).session(this);
    }

    public RequestBuilder put(String url) {
        return Requests.put(url).session(this);
    }

    public RequestBuilder head(String url) {
        return Requests.head(url).session(this);
    }

    public RequestBuilder delete(String url) {
        return Requests.delete(url).session(this);
    }

    public RequestBuilder patch(String url) {
        return Requests.patch(url).session(this);
    }


}
