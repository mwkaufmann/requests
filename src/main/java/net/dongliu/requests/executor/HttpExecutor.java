package net.dongliu.requests.executor;

import org.jetbrains.annotations.NotNull;

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
    @NotNull
    RawResponse proceed(Request request);
}
