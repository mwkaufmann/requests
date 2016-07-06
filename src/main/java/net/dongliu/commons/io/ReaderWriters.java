package net.dongliu.commons.io;

import net.dongliu.commons.exception.Exceptions;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Utils for Reader and Writer
 *
 * @author Liu Dong
 */
@ParametersAreNonnullByDefault
public class ReaderWriters {

    private static final int BUFFER_SIZE = 4 * 1024;

    /**
     * Copy reader to writer, and close reader
     */
    public static void copy(Reader reader, Writer writer) {
        char[] buffer = new char[BUFFER_SIZE];
        try (Reader r = reader) {
            int read;
            while ((read = r.read(buffer)) != -1) {
                writer.write(buffer, 0, read);
            }
        } catch (IOException e) {
            throw Exceptions.sneakyThrow(e);
        }
    }

    /**
     * Read reader to String, and then close reader
     */
    public static String readAll(Reader reader) {
        try (StringWriter writer = new StringWriter()) {
            copy(reader, writer);
            return writer.toString();
        } catch (IOException e) {
            throw Exceptions.sneakyThrow(e);
        }
    }

    /**
     * Read reader to lines, and then close reader
     */
    public static List<String> readLines(Reader reader) {
        String line;
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = buffered(reader)) {
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
            return lines;
        } catch (IOException e) {
            throw Exceptions.sneakyThrow(e);
        }
    }

    /**
     * Wrap reader to buffered reader. if reader is buffered, return its self.
     * if exception occurred, close reader
     */
    public static BufferedReader buffered(Reader reader) {
        if (reader instanceof BufferedReader) {
            return (BufferedReader) reader;
        }
        try {
            return new BufferedReader(reader);
        } catch (Throwable e) {
            Closeables.close(reader, e);
            throw e;
        }
    }


    /**
     * Wrap input stream to buffered reader. if reader is buffered, return its self.
     * if exception occurred, close input stream
     */
    public static BufferedReader buffered(InputStream input, Charset charset) {
        Reader reader;
        try {
            reader = new InputStreamReader(input, charset);
        } catch (Throwable e) {
            Closeables.close(input, e);
            throw e;
        }
        return buffered(reader);
    }

    /**
     * Skip all reader data, and finally close it
     *
     * @return the num of chars skipped
     */
    public static long skipAll(Reader reader) {
        long count = 0;
        long read;
        try (Reader r = reader) {
            while ((read = r.skip(BUFFER_SIZE)) != 0) {
                count += read;
            }
            char[] buffer = new char[BUFFER_SIZE];
            while ((read = r.read(buffer)) != -1) {
                count += read;
            }
        } catch (IOException e) {
            throw Exceptions.sneakyThrow(e);
        }
        return count;
    }
}
