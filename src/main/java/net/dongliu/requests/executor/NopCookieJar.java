package net.dongliu.requests.executor;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import net.dongliu.requests.Cookie;

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

    @NotNull
    @Override
    public List<Cookie> getCookies(URL url) {
        return Collections.emptyList();
    }

    @NotNull
    @Override
    public List<Cookie> getCookies() {
        return Collections.emptyList();
    }
}
