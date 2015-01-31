package net.dongliu.requests.exception;

import java.net.URISyntaxException;

/**
 * Runtime version for URISyntaxException
 *
 * @author Dong Liu dongliu@wandoujia.com
 */
public class RuntimeURISyntaxException extends RequestException {

    public static RuntimeURISyntaxException of(URISyntaxException e) {
        RuntimeURISyntaxException ex = new RuntimeURISyntaxException(e.getMessage(), e.getCause());
        ex.setStackTrace(e.getStackTrace());
        return ex;
    }

    public RuntimeURISyntaxException() {
    }

    public RuntimeURISyntaxException(String message) {
        super(message);
    }

    public RuntimeURISyntaxException(String message, Throwable cause) {
        super(message, cause);
    }

    public RuntimeURISyntaxException(Throwable cause) {
        super(cause);
    }

    public RuntimeURISyntaxException(String message, Throwable cause, boolean enableSuppression,
                                     boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
