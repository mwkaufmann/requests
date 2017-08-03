package net.dongliu.requests.executor;

import net.dongliu.requests.executor.huc.URLConnectionRequestProvider;

/**
 * For retrieve request client instance
 */
public class RequestProviders {

    /**
     * Get new RequestProvider instance
     */
    public static RequestProvider lookup() {
        return new URLConnectionRequestProvider();
    }
}
