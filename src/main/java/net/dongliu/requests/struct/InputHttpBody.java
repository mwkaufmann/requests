package net.dongliu.requests.struct;

import java.io.InputStream;

/**
 * @author Liu Dong
 */
public class InputHttpBody extends HttpBody<InputStream> {
    public InputHttpBody(InputStream in) {
        super(in);
    }
}
