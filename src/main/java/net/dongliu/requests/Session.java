package net.dongliu.requests;

import javax.annotation.concurrent.ThreadSafe;
import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Http request share cookies etc.
 * This class is thread-safe
 */
@ThreadSafe
public class Session {

    private Set<Cookie> cookies = Collections.emptySet();

    Session() {
    }

    static Session create(Set<Cookie> cookies) {
        Session session = new Session();
        session.updateCookie(cookies);
        return session;
    }

    synchronized void updateCookie(Set<Cookie> addCookies) {
        if (addCookies.isEmpty()) {
            return;
        }

        // new cookie will override old cookie with the same (name, domain, path) value, even it is expired
        Instant now = Instant.now();
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

    Stream<Cookie> matchedCookies(String protocol, String domain, String path) {
        Instant now = Instant.now();
        return this.cookies.stream().filter(c -> c.match(protocol, domain, path))
                .filter(c -> !c.expired(now));
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
