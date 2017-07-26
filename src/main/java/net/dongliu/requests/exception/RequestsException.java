package net.dongliu.requests.exception;

/**
 * Thrown when request failed.
 *
 * @author Liu Dong
 */
public class RequestsException extends RuntimeException {
    public RequestsException(String msg) {
        super(msg);
    }

    public RequestsException(String msg, Exception e) {
        super(msg, e);
    }

    public RequestsException(Exception e) {
        super(e);
    }
}
