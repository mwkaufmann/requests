package net.dongliu.requests;

import javax.annotation.Nullable;
import java.io.*;

/**
 * IOUtils optimized for http request
 *
 * @author Liu Dong
 */
class IOUtils {

    static final byte[] emptyByteArray = new byte[0];

    /**
     * Copy stream. input stream is closed when copy finished or error occurred
     *
     * @param expectSize if <0, mean unknown size
     * @throws UncheckedIOException
     */
    static void copy(InputStream in, OutputStream out, long expectSize) {
        int bufferSize = 1024 * 8;
        if (expectSize > 0 && expectSize < bufferSize) {
            bufferSize = (int) expectSize;
        }
        try {
            byte[] buffer = new byte[bufferSize];
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
     * @param expectSize expect data size in chars.if <0, mean unknown size
     * @throws UncheckedIOException
     */
    static void copy(Reader reader, Writer writer, long expectSize) {
        int bufferSize = 1024 * 4;
        if (expectSize > 0 && expectSize < bufferSize) {
            bufferSize = (int) expectSize;
        }
        try {
            char[] buffer = new char[bufferSize];
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
     * @param expectSize expect data size in chars.if <0, mean unknown size
     * @throws UncheckedIOException
     */
    static String toString(Reader reader, int expectSize) {
        try (StringWriter writer = expectSize > 0 ? new StringWriter(expectSize) : new StringWriter(512)) {
            copy(reader, writer, expectSize);
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
    public static byte[] toBytes(InputStream in, int expectSize) {
        try (ByteArrayOutputStream os = expectSize > 0 ?
                new ByteArrayOutputStream(expectSize) : new ByteArrayOutputStream(1024)) {
            copy(in, os, expectSize);
            return os.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
