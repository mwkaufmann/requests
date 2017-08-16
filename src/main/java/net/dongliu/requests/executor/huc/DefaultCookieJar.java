package net.dongliu.requests.executor.huc;

import net.dongliu.requests.Cookie;
import net.dongliu.requests.utils.Cookies;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;
import java.io.Serializable;
import java.net.URL;
import java.util.*;

/**
 * CookieJar that store cookie in memory, maintaining cookies following RFC 6265
 */
@ThreadSafe
class DefaultCookieJar implements CookieJar, Serializable {

    private Map<CookieKey, Cookie> cookieMap = new HashMap<>();

    @Override
    public synchronized void storeCookies(Collection<Cookie> cookies) {
        for (Cookie cookie : cookies) {
            CookieKey key = new CookieKey(cookie.getDomain(), cookie.getPath(), cookie.getName());
            cookieMap.put(key, cookie);
        }
        removeExpiredCookies();
    }

    private void removeExpiredCookies() {
        long now = System.currentTimeMillis();
        Iterator<Map.Entry<CookieKey, Cookie>> iterator = cookieMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<CookieKey, Cookie> entry = iterator.next();
            if (entry.getValue().expired(now)) {
                iterator.remove();
            }
        }
    }

    @Nonnull
    @Override
    public synchronized List<Cookie> getCookies(URL url) {
        long now = System.currentTimeMillis();
        List<Cookie> matched = new ArrayList<>();
        for (Cookie cookie : cookieMap.values()) {
            if (!Cookies.match(cookie, url.getProtocol(), url.getHost().toLowerCase(), url.getPath())) {
                continue;
            }
            if (cookie.expired(now)) {
                continue;
            }
            matched.add(cookie);
        }
        Collections.sort(matched, new Comparator<Cookie>() { // we did not sort using create time here
            @Override
            public int compare(Cookie cookie1, Cookie cookie2) {
                return cookie2.getPath().length() - cookie1.getPath().length();
            }
        });
        return matched;
    }

    @Nonnull
    @Override
    public synchronized Collection<Cookie> getCookies() {
        return new ArrayList<>(cookieMap.values());
    }

    @Immutable
    private static class CookieKey {
        @Nonnull
        private final String domain;
        @Nonnull
        private final String path;
        @Nonnull
        private final String name;

        public CookieKey(String domain, String path, String name) {
            this.domain = domain;
            this.path = path;
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            CookieKey cookieKey = (CookieKey) o;

            if (!domain.equals(cookieKey.domain)) return false;
            if (!path.equals(cookieKey.path)) return false;
            return name.equals(cookieKey.name);
        }

        @Override
        public int hashCode() {
            int result = domain.hashCode();
            result = 31 * result + path.hashCode();
            result = 31 * result + name.hashCode();
            return result;
        }
    }
}
