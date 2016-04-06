package net.dongliu.requests.exception;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * Root exception for requests.
 *
 * @author Dong Liu
 */
public class RequestException extends UncheckedIOException {

    public RequestException(IOException cause) {
        super(cause);
    }

    public RequestException(String message, IOException cause) {
        super(message, cause);
    }
}
