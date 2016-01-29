package net.dongliu.requests;

import net.dongliu.requests.exception.DuplicateBodyException;
import net.dongliu.requests.struct.BytesHttpBody;
import net.dongliu.requests.struct.HttpBody;
import net.dongliu.requests.struct.InputHttpBody;
import net.dongliu.requests.struct.StringHttpBody;

import java.io.InputStream;

/**
 * @author Liu Dong
 */
public abstract class AbstractBodyRequestBuilder<T extends AbstractBodyRequestBuilder<T>> extends RequestBuilder<T>
        implements BodyRequestBuilderInterface<T> {

    protected HttpBody httpBody;

    protected AbstractBodyRequestBuilder() {
    }

    @Override
    public T data(byte[] data) {
        checkHttpBody(BytesHttpBody.class);
        this.httpBody = new BytesHttpBody(data);
        return self();
    }

    @Override
    public T data(InputStream in) {
        checkHttpBody(InputHttpBody.class);
        this.httpBody = new InputHttpBody(in);
        return self();
    }

    @Override
    public T data(String body) {
        checkHttpBody(StringHttpBody.class);
        this.httpBody = new StringHttpBody(body);
        return self();
    }

    protected <R extends HttpBody> void checkHttpBody(Class<R> cls) {
        if (this.httpBody != null && !cls.isAssignableFrom(this.httpBody.getClass())) {
            throw new DuplicateBodyException(this.httpBody.getClass());
        }
    }
}
