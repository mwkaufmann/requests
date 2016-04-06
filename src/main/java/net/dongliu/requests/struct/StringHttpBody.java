package net.dongliu.requests.struct;

import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.StringEntity;

import java.nio.charset.Charset;

/**
 * @author Liu Dong
 */
public class StringHttpBody extends HttpBody<String> {

    public StringHttpBody(String body) {
        super(body);
    }

    @Override
    public AbstractHttpEntity createEntity(Charset charset) {
        return new StringEntity(getBody(), charset);
    }
}
