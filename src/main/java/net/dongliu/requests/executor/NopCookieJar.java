package net.dongliu.requests.executor;

import net.dongliu.requests.Cookie;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Cookie jar that do nothing. Used for plain request.
 */
class NopCookieJar implements CookieJar {

    static final NopCookieJar instance = new NopCookieJar();

    private NopCookieJar() {
    }

    @Override
    public void storeCookies(Collection<Cookie> cookies) {

    }

    @Override
    public List<Cookie> getCookies(URL url) {
        return Collections.emptyList();
    }

    @Override
    public List<Cookie> getCookies() {
        return Collections.emptyList();
    }
}
