package net.dongliu.requests.struct;

import org.apache.http.annotation.Immutable;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.ByteArrayEntity;

import java.nio.charset.Charset;

/**
 * @author Liu Dong
 */
@Immutable
public class BytesHttpBody extends HttpBody<byte[]> {
    public BytesHttpBody(byte[] body) {
        super(body);
    }

    @Override
    public AbstractHttpEntity createEntity(Charset charset) {
        return new ByteArrayEntity(getBody());
    }
}
