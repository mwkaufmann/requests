package net.dongliu.requests;

import net.dongliu.requests.exception.RequestException;

import java.io.File;
import java.nio.charset.Charset;

public interface Executable {
    /**
     * set custom handler to handle http response
     */
    <R> Response<R> handle(ResponseHandler<R> handler) throws RequestException;

    /**
     * Get http response for return text result.
     * Decode response body to text with charset provided
     */
    Response<String> text(Charset responseCharset) throws RequestException;

    /**
     * Get http response for return text result.
     * Decode response body to text with charset get from response header
     */
    Response<String> text() throws RequestException;

    /**
     * get http response for return byte array result.
     */
    Response<byte[]> bytes() throws RequestException;

    /**
     * get http response for write response body to file.
     * only save to file when return status is 200, otherwise return response with null body.
     */
    Response<File> file(File file) throws RequestException;

    /**
     * get http response for write response body to file.
     * only save to file when return status is 200, otherwise return response with null body.
     */
    Response<File> file(String filePath) throws RequestException;
}
