package net.dongliu.requests.executor;

import javax.annotation.Nonnull;

/**
 * Only for internal use
 */
class URLConnectionExecutorFactory extends RequestExecutorFactory {
    static final RequestExecutorFactory instance = new URLConnectionExecutorFactory();

    @Nonnull
    @Override
    public SessionContext newSessionContext() {
        return new SessionContext(new DefaultCookieJar());
    }

    @Nonnull
    @Override
    public HttpExecutor getHttpExecutor() {
        return new URLConnectionExecutor();
    }
}
