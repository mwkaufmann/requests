package net.dongliu.requests.body;

import net.dongliu.requests.Requests;
import net.dongliu.commons.json.JsonLookup;
import net.dongliu.commons.json.JsonProvider;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;

import static net.dongliu.requests.HttpHeaders.CONTENT_TYPE_JSON;

/**
 * @author Liu Dong
 */
class JsonRequestBody<T> extends RequestBody<T> {

    JsonRequestBody(T body) {
        super(body, CONTENT_TYPE_JSON, true);
    }

    @Override
    public void writeBody(OutputStream os, Charset charset) throws IOException {
        try (Writer writer = new OutputStreamWriter(os, charset)) {
            JsonLookup.getInstance().lookup().marshal(writer, getBody());
        }
    }
}
