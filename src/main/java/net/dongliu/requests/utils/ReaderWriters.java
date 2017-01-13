package net.dongliu.requests.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Utils for Reader and Writer
 *
 * @author Liu Dong
 */
public class ReaderWriters {

    private static final int BUFFER_SIZE = 4 * 1024;

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
            Closeables.closeQuietly(reader);
        }
    }

    /**
     * Read reader to String, and then close reader
     */
    public static String readAll(Reader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        char[] buffer = new char[BUFFER_SIZE];
        try {
            int read;
            while ((read = reader.read(buffer)) != -1) {
                sb.append(buffer, 0, read);
            }
        } finally {
            Closeables.closeQuietly(reader);
        }
        return sb.toString();
    }

    /**
     * Read reader to lines, and then close reader
     */
    public static List<String> readLines(Reader reader) throws IOException {
        String line;
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = buffered(reader)) {
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
            return lines;
        } finally {
            Closeables.closeQuietly(reader);
        }
    }

    /**
     * Wrap reader to buffered reader. if reader is buffered, return its self.
     */
    public static BufferedReader buffered(Reader reader) {
        if (reader instanceof BufferedReader) {
            return (BufferedReader) reader;
        }
        return new BufferedReader(reader);
    }

    /**
     * Skip all reader data, and finally close it
     *
     * @return the num of chars skipped
     */
    public static long skipAll(Reader reader) throws IOException {
        long count = 0;
        long read;
        try {
            while ((read = reader.skip(BUFFER_SIZE)) != 0) {
                count += read;
            }
            int c = reader.read();
            if (c == -1) {
                return count;
            }
            char[] buffer = new char[BUFFER_SIZE];
            while ((read = reader.read(buffer)) != -1) {
                count += read;
            }
        } finally {
            Closeables.closeQuietly(reader);
        }
        return count;
    }
}
