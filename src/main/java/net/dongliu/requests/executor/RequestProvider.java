package net.dongliu.requests.executor;

import net.dongliu.requests.Client;
import net.dongliu.requests.ClientBuilder;

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
    Client newClient(ClientBuilder builder);

}
