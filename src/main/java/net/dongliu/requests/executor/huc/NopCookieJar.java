package net.dongliu.requests.executor.huc;

import jdk.nashorn.internal.ir.annotations.Immutable;
import net.dongliu.requests.Cookie;

import javax.annotation.Nonnull;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Cookie jar that do nothing. Used for plain request.
 */
@Immutable
class NopCookieJar implements CookieJar {

    static final NopCookieJar instance = new NopCookieJar();

    private NopCookieJar() {
    }

    @Override
    public void storeCookies(Collection<Cookie> cookies) {

    }

    @Nonnull
    @Override
    public List<Cookie> getCookies(URL url) {
        return Collections.emptyList();
    }

    @Nonnull
    @Override
    public Collection<Cookie> getCookies() {
        return Collections.emptyList();
    }
}
