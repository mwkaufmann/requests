package net.dongliu.requests;


import javax.annotation.concurrent.Immutable;
import java.io.Serializable;
import java.util.List;

/**
 * Response with transformed result
 *
 * @author Liu Dong
 */
@Immutable
public class Response<T> extends AbstractResponse implements Serializable {
    private static final long serialVersionUID = 5956373495731090956L;
    private final T body;

    public Response(String url, int statusCode, List<Cookie> cookies, Headers headers, T body) {
        super(url, statusCode, cookies, headers);
        this.body = body;
    }

    public T getBody() {
        return body;
    }
}
