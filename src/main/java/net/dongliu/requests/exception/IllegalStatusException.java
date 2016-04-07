package net.dongliu.requests.exception;

/**
 * Exception used to indicate the http status is not expected
 *
 * @author Liu Dong
 */
public class IllegalStatusException extends RuntimeException {

    public IllegalStatusException(int status) {
        super("Unexpected status:" + status);
    }
}
