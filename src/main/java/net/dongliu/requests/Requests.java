package net.dongliu.requests;

import net.dongliu.requests.exception.RequestException;

/**
 * Convenient utils method for to process common http request
 *
 * @author Dong Liu
 */
public class Requests {

    /**
     * get method
     */
    public static HeadOnlyRequestBuilder get(String url) throws RequestException {
        return client().get(url);
    }

    /**
     * head method
     */
    public static HeadOnlyRequestBuilder head(String url) throws RequestException {
        return client().head(url);
    }

    /**
     * get url, and return content
     */
    public static PostRequestBuilder post(String url) throws RequestException {
        return client().post(url);
    }

    /**
     * put method
     */
    public static BodyRequestBuilder put(String url) throws RequestException {
        return client().put(url);
    }

    /**
     * delete method
     */
    public static HeadOnlyRequestBuilder delete(String url) throws RequestException {
        return client().delete(url);
    }

    /**
     * options method
     */
    public static HeadOnlyRequestBuilder options(String url) throws RequestException {
        return client().options(url);
    }

    /**
     * patch method
     */
    public static BodyRequestBuilder patch(String url) throws RequestException {
        return client().patch(url);
    }

    /**
     * trace method
     */
    public static HeadOnlyRequestBuilder trace(String url) throws RequestException {
        return client().trace(url);
    }

    private static Client client() {
        return Client.single().closeOnRequstFinished(true).build();
    }
}