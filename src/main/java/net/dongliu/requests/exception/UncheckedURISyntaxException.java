package net.dongliu.requests.exception;

import java.net.URISyntaxException;

/**
 * Unchecked version of URISyntaxException
 *
 * @author Liu Dong
 */
public class UncheckedURISyntaxException extends RuntimeException {
    public UncheckedURISyntaxException(URISyntaxException cause) {
        super(cause);
    }
}
