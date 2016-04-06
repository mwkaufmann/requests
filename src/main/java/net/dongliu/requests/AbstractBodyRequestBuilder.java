package net.dongliu.requests;

import net.dongliu.requests.struct.HttpBody;

/**
 * @author Liu Dong
 */
public abstract class AbstractBodyRequestBuilder<T extends AbstractBodyRequestBuilder<T>> extends RequestBuilder<T>
        implements IBodyRequestBuilder<T> {

    protected HttpBody httpBody;

    protected AbstractBodyRequestBuilder() {
    }

    @Override
    public T body(HttpBody body) {
        this.httpBody = body;
        return self();
    }
}
