package net.dongliu.requests;

import net.dongliu.requests.struct.BytesHttpBody;
import net.dongliu.requests.struct.HttpBody;
import net.dongliu.requests.struct.InputHttpBody;
import net.dongliu.requests.struct.StringHttpBody;

import java.io.InputStream;

/**
 * Convenient Methods to set http body
 *
 * @param <T>
 */
public interface IBodyRequestBuilder<T> extends IBaseRequestBuilder<T> {
    /**
     * Set http body data for Post/Put request
     *
     * @param data the data to post
     */
    default T body(byte[] data) {
        return body(new BytesHttpBody(data));
    }

    /**
     * Set http data from inputStream for Post/Put request
     */
    default T body(InputStream in) {
        return body(new InputHttpBody(in));
    }

    /**
     * Set http data with text.
     * The text string will be encoded, default using utf-8, set charset with charset(Charset charset) method
     */
    default T body(String body) {
        return body(new StringHttpBody(body));
    }

    T body(HttpBody body);
}
