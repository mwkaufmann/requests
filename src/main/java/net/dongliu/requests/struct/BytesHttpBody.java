package net.dongliu.requests.struct;

/**
 * @author Liu Dong
 */
public class BytesHttpBody extends HttpBody<byte[]> {
    public BytesHttpBody(byte[] body) {
        super(body);
    }
}
