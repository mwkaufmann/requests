package net.dongliu.requests.struct;

import org.apache.http.HttpEntity;
import org.apache.http.annotation.Immutable;

import java.nio.charset.Charset;

/**
 * Http request body
 *
 * @author Liu Dong
 */
@Immutable
public abstract class HttpBody<T> {
    private final T body;

    protected HttpBody(T body) {
        this.body = body;
    }

    public T getBody() {
        return body;
    }

    /**
     * Generate http client request entity
     */
    public abstract HttpEntity createEntity(Charset charset);
}
