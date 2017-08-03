package net.dongliu.requests.executor;

import net.dongliu.requests.Interceptor;
import net.dongliu.requests.RawResponse;
import net.dongliu.requests.Request;

import javax.annotation.Nonnull;

/**
 * Http executor
 *
 * @author Liu Dong
 */
public interface HttpExecutor extends Interceptor.InvocationTarget {
    /**
     * Process the request, and return response
     */
    @Nonnull
    RawResponse proceed(Request request);
}
