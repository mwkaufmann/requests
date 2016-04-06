package net.dongliu.requests;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;

/**
 * @author Liu Dong
 */
class IOUtils {

    static void copy(InputStream in, OutputStream out) {
        try {
            byte[] buffer = new byte[1024 * 8];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } finally {
            closeQuietly(in);
        }
    }

    static void closeQuietly(AutoCloseable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception ignore) {
            }
        }
    }
}
