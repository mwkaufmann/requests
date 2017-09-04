package net.dongliu.requests;

import javax.annotation.Nonnull;

/**
 * Http request Interceptor
 *
 * @author Liu Dong
 */
public interface Interceptor {

    /**
     * Intercept http request process.
     *
     * @param target  used to proceed request with remains interceptors and url executor
     * @param request the http request
     * @return http response from target invoke, or replaced / wrapped by implementation
     */
    @Nonnull
    RawResponse intercept(InvocationTarget target, Request request);


    interface InvocationTarget {
        /**
         * Process the request, and return response
         */
        @Nonnull
        RawResponse proceed(Request request);
    }
}
