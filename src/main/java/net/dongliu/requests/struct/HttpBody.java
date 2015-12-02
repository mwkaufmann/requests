package net.dongliu.requests.struct;

/**
 * @author Liu Dong
 */
public abstract class HttpBody<T> {
    private final T body;

    protected HttpBody(T body) {
        this.body = body;
    }

    public T getBody() {
        return body;
    }
}
