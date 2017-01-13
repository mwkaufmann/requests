package net.dongliu.requests.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Utils method for bytes io
 *
 * @author Liu Dong
 */
public class InputOutputs {


    private static byte[] empty = {};

    private static final int BUFFER_SIZE = 8 * 1024;

    /**
     * Copy input stream to output stream, and close input
     */
    public static void copy(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        try {
            int read;
            while ((read = input.read(buffer)) != -1) {
                output.write(buffer, 0, read);
            }
        } finally {
            Closeables.closeQuietly(input);
        }
    }

    /**
     * Read input to bytes, and then close input.
     *
     * @param initialSize the initial size to be used
     */
    public static byte[] readAll(InputStream input, int initialSize) throws IOException {
        initialSize = Math.max(initialSize, 32);
        int total = 0;
        int segmentSize = initialSize;
        List<byte[]> dataList = new ArrayList<>();

        try {
            while (true) {
                int b = input.read();
                if (b == -1) {
                    break;
                }
                byte[] segment = new byte[segmentSize];
                if (total > 0) {
                    segmentSize = Math.min(segmentSize * 2, 1024 * 1024);
                }
                segment[0] = (byte) b;
                int toRead = segment.length - 1;
                int read = readExact(input, segment, 1, toRead);
                if (Integer.MAX_VALUE - read < total) {
                    throw new RuntimeException("Data large than array max size");
                }
                total += read + 1;
                dataList.add(segment);
                if (read != toRead) {
                    break;
                }
            }
        } finally {
            Closeables.closeQuietly(input);
        }

        if (dataList.size() == 0) {
            return empty;
        }

        if (dataList.size() == 1) {
            if (total == initialSize) {
                return dataList.get(0);
            }
        }
        byte[] data = new byte[total];
        int offset = 0;
        for (byte[] segment : dataList) {
            int size = Math.min(segment.length, total - offset);
            System.arraycopy(segment, 0, data, offset, size);
            offset += size;
        }
        return data;
    }

    /**
     * Read input to bytes, and then close input
     */
    public static byte[] readAll(InputStream input) throws IOException {
        return readAll(input, 32);
    }

    /**
     * Read exactly data with size.
     *
     * @return the size read; may less than len if reach the end for stream
     */
    public static int readExact(InputStream input, byte[] data, int offset, int len) throws IOException {
        int read;
        int total = 0;
        while (len > 0 && (read = input.read(data, offset, len)) != -1) {
            total += read;
            offset += read;
            len -= read;
        }
        return total;
    }

    /**
     * Read exactly data with size.
     *
     * @return the size read; may less than array len if reach the end for stream
     */
    public static int readExact(InputStream input, byte[] data) throws IOException {
        return readExact(input, data, 0, data.length);
    }


    /**
     * Read all data from input and discard. The input stream would be closed when read finished.
     *
     * @return the bytes consumed
     */
    public static long consumeAll(InputStream input) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        long total = 0;
        int read;
        try {
            while ((read = input.read(buffer)) > 0) {
                total += read;
            }
        } finally {
            Closeables.closeQuietly(input);
        }
        return total;
    }

    /**
     * Skip data with exact size
     *
     * @return the skipped size; may less than len if reach the end of stream
     */
    public static long skipExact(InputStream input, long len) throws IOException {
        long count = 0;
        long read;
        while (len > 0 && (read = input.skip(len)) != 0) {
            count += read;
            len -= read;
        }
        if (len == 0) {
            return count;
        }

        byte[] buffer = new byte[BUFFER_SIZE];
        while (len > 0 && (read = input.read(buffer, 0, (int) Math.min(len, buffer.length))) != -1) {
            count += read;
            len -= read;
        }
        return count;
    }

    /**
     * Skip all input data, and finally close it
     *
     * @return the num of bytes skipped
     */
    public static long skipAll(InputStream input) throws IOException {
        long count = 0;
        long read;
        try {
            while ((read = input.skip(Math.max(input.available(), BUFFER_SIZE))) != 0) {
                count += read;
            }
            int b = input.read();
            if (b == -1) {
                return count;
            }
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((read = input.read(buffer)) != -1) {
                count += read;
            }
        } finally {
            Closeables.closeQuietly(input);
        }
        return count;
    }
    
}
