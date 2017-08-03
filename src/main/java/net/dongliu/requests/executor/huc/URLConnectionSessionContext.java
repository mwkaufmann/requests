package net.dongliu.requests.executor.huc;

import net.dongliu.requests.executor.SessionContext;

class URLConnectionSessionContext implements SessionContext {
    private final CookieJar cookieJar;

    public URLConnectionSessionContext(CookieJar cookieJar) {
        this.cookieJar = cookieJar;
    }

    public CookieJar getCookieJar() {
        return cookieJar;
    }
}
