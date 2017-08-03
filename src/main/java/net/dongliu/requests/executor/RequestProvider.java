package net.dongliu.requests.executor;

import javax.annotation.Nonnull;

/**
 * Request Client interface
 */
public interface RequestProvider {

    /**
     * Create new session context
     */
    @Nonnull
    SessionContext newSessionContext();

    @Nonnull
    HttpExecutor httpExecutor();
}
