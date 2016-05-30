package net.dongliu.requests;

import java.io.*;

/**
 * @author Liu Dong
 */
public class IOUtils {
    private static final int BUFFER_SIZE = 1024 * 4;

    static void closeQuietly(AutoCloseable closeable) {
        try {
            closeable.close();
        } catch (Exception ignore) {

        }
    }

    /**
     * Copy reader to writer, and close reader
     */
    public static void copy(Reader reader, Writer writer) throws IOException {
        char[] buffer = new char[BUFFER_SIZE];
        try {
            int read;
            while ((read = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, read);
            }
        } finally {
            closeQuietly(reader);
        }
    }

    /**
     * Read reader to String, and then close reader
     *
     * @throws IOException
     */
    public static String readAll(Reader reader) throws IOException {
        try (StringWriter writer = new StringWriter()) {
            copy(reader, writer);
            return writer.toString();
        }
    }


    /**
     * Copy input stream to ouput stream, and close input
     */
    public static void copy(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        try {
            int read;
            while ((read = input.read(buffer)) != -1) {
                output.write(buffer, 0, read);
            }
        } finally {
            closeQuietly(input);
        }
    }

    /**
     * Read input to bytes, and then close input
     *
     * @throws IOException
     */
    public static byte[] readAll(InputStream input) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            copy(input, bos);
            return bos.toByteArray();
        }
    }
}
