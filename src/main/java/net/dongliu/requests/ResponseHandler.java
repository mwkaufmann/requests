package net.dongliu.requests;

import java.io.IOException;
import java.io.InputStream;

/**
 * Handler raw response body, convert to result T
 */
public interface ResponseHandler<T> {

    /**
     * Handler raw response body, convert to result T
     */
    T handle(int statusCode, Headers headers, InputStream in) throws IOException;
}
