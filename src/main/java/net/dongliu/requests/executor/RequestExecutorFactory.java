package net.dongliu.requests.executor;

import javax.annotation.Nonnull;

/**
 * Request Client interface
 */
public abstract class RequestExecutorFactory {

    public static RequestExecutorFactory getInstance() {
        return URLConnectionExecutorFactory.instance;
    }

    /**
     * Create new session context
     */
    @Nonnull
    public abstract SessionContext newSessionContext();

    @Nonnull
    public abstract HttpExecutor getHttpExecutor();
}
