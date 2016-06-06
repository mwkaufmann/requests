package net.dongliu.requests;

/**
 * Http request Interceptor
 *
 * @author Liu Dong
 */
public interface Interceptor {

    /**
     * Intercept http request process
     */
    RawResponse intercept(InvocationTarget target, HttpRequest request);


    interface InvocationTarget {
        /**
         * Process the request, and return response
         */
        RawResponse proceed(HttpRequest request);
    }
}
