package net.dongliu.requests.exception;

import net.dongliu.requests.struct.HttpBody;

/**
 * @author Liu Dong
 */
public class DuplicateBodyException extends RequestException {

    public DuplicateBodyException(Class<? extends HttpBody> cls) {
        super("Body has been set via " + cls.getName());
    }
}
