package net.dongliu.requests;

import net.dongliu.requests.exception.RequestException;
import net.dongliu.requests.struct.Method;

/**
 * Convenient utils method for to process common http request
 *
 * @author Dong Liu
 */
public class Requests {

    /**
     * get method
     */
    public static MixinHeadOnlyRequestBuilder get(String url) throws RequestException {
        return new MixinHeadOnlyRequestBuilder().method(Method.GET).url(url);
    }

    /**
     * head method
     */
    public static MixinHeadOnlyRequestBuilder head(String url) throws RequestException {
        return new MixinHeadOnlyRequestBuilder().method(Method.HEAD).url(url);
    }

    /**
     * get url, and return content
     */
    public static MixinPostRequestBuilder post(String url) throws RequestException {
        return new MixinPostRequestBuilder().method(Method.POST).url(url);
    }

    /**
     * put method
     */
    public static MixinBodyRequestBuilder put(String url) throws RequestException {
        return new MixinBodyRequestBuilder().method(Method.PUT).url(url);
    }

    /**
     * delete method
     */
    public static MixinHeadOnlyRequestBuilder delete(String url) throws RequestException {
        return new MixinHeadOnlyRequestBuilder().method(Method.DELETE).url(url);
    }

    /**
     * options method
     */
    public static MixinHeadOnlyRequestBuilder options(String url) throws RequestException {
        return new MixinHeadOnlyRequestBuilder().method(Method.OPTIONS).url(url);
    }

    /**
     * patch method
     */
    public static MixinBodyRequestBuilder patch(String url) throws RequestException {
        return new MixinBodyRequestBuilder().method(Method.PATCH).url(url);
    }

    /**
     * trace method
     */
    public static MixinHeadOnlyRequestBuilder trace(String url) throws RequestException {
        return new MixinHeadOnlyRequestBuilder().method(Method.TRACE).url(url);
    }

}