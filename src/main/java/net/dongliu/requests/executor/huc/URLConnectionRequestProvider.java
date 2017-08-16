package net.dongliu.requests.executor.huc;

import net.dongliu.requests.Client;
import net.dongliu.requests.ClientBuilder;
import net.dongliu.requests.executor.RequestProvider;
import net.dongliu.requests.executor.SessionContext;

import javax.annotation.Nonnull;

/**
 * Only for internal use
 */
public class URLConnectionRequestProvider implements RequestProvider {
    @Nonnull
    @Override
    public SessionContext newSessionContext() {
        return new URLConnectionSessionContext(new DefaultCookieJar());
    }

    @Nonnull
    @Override
    public Client newClient(ClientBuilder builder) {
        return new URLConnectionClient(builder);
    }
}
