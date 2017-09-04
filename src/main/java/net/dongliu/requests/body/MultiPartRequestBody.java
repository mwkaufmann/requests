package net.dongliu.requests.body;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Collection;

/**
 * @author Liu Dong
 */
class MultiPartRequestBody extends RequestBody<Collection<? extends Part>> {
    private static final String BOUNDARY = "********************" + System.currentTimeMillis();
    private static final String LINE_END = "\r\n";
    private static final long serialVersionUID = -2150328570818986957L;

    public MultiPartRequestBody(Collection<? extends Part> body) {
        super(body, "multipart/form-data; boundary=" + BOUNDARY, false);
    }

    @Override
    public void writeBody(OutputStream os, Charset charset) throws IOException {
        OutputStream output = new NonCloseOutputStream(os);
        try (Writer writer = new OutputStreamWriter(output)) {
            for (Part part : getBody()) {
                RequestBody body = part.getRequestBody();
                String name = part.getName();
                String fileName = part.getFileName();

                writeBoundary(writer);

                writer.write("Content-Disposition: form-data; name=\"" + name + "\"");
                if (fileName != null && !fileName.isEmpty()) {
                    writer.write("; filename=\"" + fileName + '"');
                }
                writer.write(LINE_END);
                if (body.getContentType() != null && !body.getContentType().isEmpty()) {
                    writer.write("Content-Type: " + body.getContentType());
                    if (body.isIncludeCharset()) {
                        writer.write("; charset=" + charset.name().toLowerCase());
                    }
                    writer.write(LINE_END);
                }
                writer.write(LINE_END);
                writer.flush();

                body.writeBody(output, charset);
                writer.write(LINE_END);
                writer.flush();
                output.flush();
            }
            writer.write("--");
            writer.write(BOUNDARY);
            writer.write("--");
            writer.write(LINE_END);
        } finally {
            output.close();
        }
    }

    private void writeBoundary(Writer writer) throws IOException {
        writer.write("--");
        writer.write(BOUNDARY);
        writer.write(LINE_END);
    }

    private static class NonCloseOutputStream extends FilterOutputStream {

        public NonCloseOutputStream(OutputStream out) {
            super(out);
        }

        @Override
        public void close() throws IOException {
        }
    }

}
