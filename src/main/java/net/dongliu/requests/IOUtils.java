package net.dongliu.requests;

import javax.annotation.Nullable;
import java.io.*;

/**
 * @author Liu Dong
 */
class IOUtils {

    static final byte[] emptyByteArray = new byte[0];

    /**
     * Copy stream. input stream is closed when copy finished or error occurred
     *
     * @throws UncheckedIOException
     */
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

    /**
     * Copy reader/writer. reader is closed when copy finished or error occurred
     *
     * @throws UncheckedIOException
     */
    static void copy(Reader reader, Writer writer) {
        try {
            char[] buffer = new char[1024 * 4];
            int len;
            while ((len = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, len);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } finally {
            closeQuietly(reader);
        }
    }

    /**
     * Read reader to string, and close reader finally
     *
     * @throws UncheckedIOException
     */
    static String toString(Reader reader) {
        try (StringWriter writer = new StringWriter()) {
            copy(reader, writer);
            return writer.toString();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    static void closeQuietly(@Nullable AutoCloseable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception ignore) {
            }
        }
    }

    /**
     * Read stream to byte array
     */
    public static byte[] toBytes(InputStream in) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            copy(in, os);
            return os.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
