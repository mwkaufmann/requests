package net.dongliu.requests.body;

import net.dongliu.commons.io.InputOutputs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import static net.dongliu.requests.HttpHeaders.CONTENT_TYPE_BINARY;

/**
 * @author Liu Dong
 */
class StreamRequestBody extends RequestBody<InputStream> {
    StreamRequestBody(InputStream body) {
        super(body, CONTENT_TYPE_BINARY, false);
    }

    @Override
    public void writeBody(OutputStream os, Charset charset) throws IOException {
        InputOutputs.copy(getBody(), os);
    }
}
