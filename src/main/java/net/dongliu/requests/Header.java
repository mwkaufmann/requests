package net.dongliu.requests;

import java.util.Objects;

/**
 * Http header
 */
public class Header extends Parameter<String> {
    private static final long serialVersionUID = 4314480501179865952L;

    public Header(String key, String value) {
        super(key, value);
    }

    /**
     * Create new header
     */
    public Header(String name, Object value) {
        this(name, Objects.requireNonNull(value).toString());
    }
}
