package net.dongliu.requests.executor.huc;

import net.dongliu.requests.executor.SessionContext;

import java.io.Serializable;

class URLConnectionSessionContext implements SessionContext, Serializable {
    private final CookieJar cookieJar;

    public URLConnectionSessionContext(CookieJar cookieJar) {
        this.cookieJar = cookieJar;
    }

    public CookieJar getCookieJar() {
        return cookieJar;
    }
}
