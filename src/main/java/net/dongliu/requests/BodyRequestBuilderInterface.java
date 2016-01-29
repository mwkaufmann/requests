package net.dongliu.requests;

import java.io.InputStream;

public interface BodyRequestBuilderInterface<T> extends BaseRequestBuilderInterface<T> {
    /**
     * Set http body data for Post/Put request
     *
     * @param data the data to post
     */
    T data(byte[] data);

    /**
     * Set http data from inputStream for Post/Put request
     */
    T data(InputStream in);

    /**
     * Set http data with text.
     * The text string will be encoded, default using utf-8, set charset with charset(Charset charset) method
     */
    T data(String body);
}
