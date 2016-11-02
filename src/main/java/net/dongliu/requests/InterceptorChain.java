package net.dongliu.requests;

import java.util.List;

/**
 * @author Liu Dong
 */
class InterceptorChain implements Interceptor.InvocationTarget {
    private final List<? extends Interceptor> interceptorList;
    private final HttpExecutor httpExecutor;
    // use index to advance interceptor, avoid interceptor chain creation
    private int index = 0;

    public InterceptorChain(List<? extends Interceptor> interceptorList, HttpExecutor httpExecutor) {
        this.interceptorList = interceptorList;
        this.httpExecutor = httpExecutor;
    }

    @Override
    public RawResponse proceed(Request request) {
        if (index == interceptorList.size()) {
            return httpExecutor.proceed(request);
        }
        Interceptor interceptor = interceptorList.get(index);
        index++;
        return interceptor.intercept(this, request);
    }

}
