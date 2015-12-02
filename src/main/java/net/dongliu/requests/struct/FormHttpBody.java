package net.dongliu.requests.struct;

import java.util.List;

/**
 * @author Liu Dong
 */
public class FormHttpBody extends HttpBody<List<Parameter>> {
    public FormHttpBody(List<Parameter> body) {
        super(body);
    }
}
