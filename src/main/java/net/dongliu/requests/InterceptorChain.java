package net.dongliu.requests;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import net.dongliu.requests.executor.HttpExecutor;

/**
 * @author Liu Dong
 */
class InterceptorChain implements Interceptor.InvocationTarget {
    private final List<? extends Interceptor> interceptorList;
    private final HttpExecutor httpExecutor;

    public InterceptorChain(List<? extends Interceptor> interceptorList, HttpExecutor httpExecutor) {
        this.interceptorList = interceptorList;
        this.httpExecutor = httpExecutor;
    }

    @NotNull
    @Override
    public RawResponse proceed(Request request) {
        if (interceptorList.isEmpty()) {
            return httpExecutor.proceed(request);
        }
        Interceptor interceptor = interceptorList.get(0);
        InterceptorChain chain = new InterceptorChain(interceptorList.subList(1, interceptorList.size()), httpExecutor);
        return interceptor.intercept(chain, request);
    }

}
