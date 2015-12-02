package net.dongliu.requests;

import net.dongliu.requests.exception.RequestException;
import net.dongliu.requests.struct.Method;

/**
 * construct and execute http requests
 *
 * @author Dong Liu
 */
public class Requests {

    /**
     * get method
     */
    public static BaseRequestBuilder get(String url) throws RequestException {
        return new BaseRequestBuilder().url(url).method(Method.GET);
    }

    /**
     * head method
     */
    public static BaseRequestBuilder head(String url) throws RequestException {
        return new BaseRequestBuilder().url(url).method(Method.HEAD);
    }

    /**
     * get url, and return content
     */
    public static PostRequestBuilder post(String url) throws RequestException {
        return new PostRequestBuilder().url(url).method(Method.POST);
    }

    /**
     * put method
     */
    public static BodyRequestBuilder put(String url) throws RequestException {
        return new BodyRequestBuilder().url(url).method(Method.PUT);
    }

    /**
     * delete method
     */
    public static BaseRequestBuilder delete(String url) throws RequestException {
        return new BaseRequestBuilder().url(url).method(Method.DELETE);
    }

    /**
     * options method
     */
    public static BaseRequestBuilder options(String url) throws RequestException {
        return new BaseRequestBuilder().url(url).method(Method.OPTIONS);
    }

    /**
     * patch method
     */
    public static BodyRequestBuilder patch(String url) throws RequestException {
        return new BodyRequestBuilder().url(url).method(Method.PATCH);
    }

    /**
     * trace method
     */
    public static BaseRequestBuilder trace(String url) throws RequestException {
        return new BaseRequestBuilder().url(url).method(Method.TRACE);
    }
//
//    /**
//     * connect
//     */
//    public static RequestBuilder connect(String url) throws InvalidUrlException {
//        return newBuilder(url).method(Method.CONNECT);
//    }

    /**
     * create a session. session can do request as Requests do, and keep cookies to maintain a http session
     */
    public static Session session() {
        return new Session(null);
    }

}