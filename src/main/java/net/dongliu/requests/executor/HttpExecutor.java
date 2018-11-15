package net.dongliu.requests.executor;

import net.dongliu.commons.annotation.NonNull;
import net.dongliu.requests.Interceptor;
import net.dongliu.requests.RawResponse;
import net.dongliu.requests.Request;

/**
 * Http executor
 *
 * @author Liu Dong
 */
public interface HttpExecutor extends Interceptor.InvocationTarget {
    /**
     * Process the request, and return response
     */
    @NonNull
    RawResponse proceed(Request request);
}
