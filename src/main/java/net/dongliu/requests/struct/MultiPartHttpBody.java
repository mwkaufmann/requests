package net.dongliu.requests.struct;

import java.util.List;

/**
 * @author Liu Dong
 */
public class MultiPartHttpBody extends HttpBody<List<MultiPart>> {
    public MultiPartHttpBody(List<MultiPart> body) {
        super(body);
    }
}
