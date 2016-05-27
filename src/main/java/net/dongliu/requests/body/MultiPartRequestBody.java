package net.dongliu.requests.body;

import net.dongliu.commons.Strings;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Collection;

/**
 * @author Liu Dong
 */
class MultiPartRequestBody extends RequestBody<Collection<? extends Part>> {
    private static final String BOUNDARY = Strings.repeat("*", 20) + System.currentTimeMillis();
    private static final String LINE_END = "\r\n";

    public MultiPartRequestBody(Collection<? extends Part> body) {
        super(body, "multipart/form-data; boundary=" + BOUNDARY, false);
    }

    // TODO: os was closed when method finished. However it does not matter current
    @Override
    public void writeBody(OutputStream os, Charset charset) throws IOException {
        try (Writer writer = new OutputStreamWriter(os)) {
            for (Part part : getBody()) {
                RequestBody body = part.getRequestBody();
                String name = part.getName();
                String fileName = part.getFileName();

                writeBoundary(writer);

                writer.write("Content-Disposition: form-data; name=\"" + name + "\"");
                if (fileName != null) {
                    writer.write("; filename=\"" + fileName + '"');
                }
                writer.write(LINE_END);
                if (body.getContentType() != null) {
                    writer.write("Content-Type: " + body.getContentType());
                    if (body.isIncludeCharset()) {
                        writer.write("; charset=" + charset.name().toLowerCase());
                    }
                    writer.write(LINE_END);
                }
                writer.write(LINE_END);
                writer.flush();

                body.writeBody(os, charset);
                writer.write(LINE_END);
                writer.flush();
                os.flush();
            }
            writer.write("--");
            writer.write(BOUNDARY);
            writer.write("--");
            writer.write(LINE_END);
        }
    }

    private void writeBoundary(Writer writer) throws IOException {
        writer.write("--");
        writer.write(BOUNDARY);
        writer.write(LINE_END);
    }

}
