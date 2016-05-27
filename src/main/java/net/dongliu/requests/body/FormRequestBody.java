package net.dongliu.requests.body;

import net.dongliu.commons.collection.Pair;
import net.dongliu.requests.URIEncoder;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Collection;

import static net.dongliu.requests.HttpHeaders.CONTENT_TYPE_FORM_ENCODED;

/**
 * @author Liu Dong
 */
class FormRequestBody extends RequestBody<Collection<? extends Pair<String, String>>> {
    FormRequestBody(Collection<? extends Pair<String, String>> body) {
        super(body, CONTENT_TYPE_FORM_ENCODED, true);
    }

    @Override public void writeBody(OutputStream os, Charset charset) throws IOException {
        String content = URIEncoder.encodeForms(getBody(), charset);
        try (Writer writer = new OutputStreamWriter(os, charset)) {
            writer.write(content);
        }
    }
}
