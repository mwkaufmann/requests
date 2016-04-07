package net.dongliu.requests.struct;

import org.apache.http.annotation.Immutable;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.InputStreamEntity;

import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * @author Liu Dong
 */
@Immutable
public class InputHttpBody extends HttpBody<InputStream> {
    public InputHttpBody(InputStream in) {
        super(in);
    }

    @Override
    public AbstractHttpEntity createEntity(Charset charset) {
        return new InputStreamEntity(getBody());
    }
}
