package net.dongliu.requests;

import javax.annotation.Nullable;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Utils method for io. for internal use only
 *
 * @author Liu Dong
 */
public class InternalIOUtils {


    private static byte[] empty = {};

    private static final int BUFFER_SIZE = 8 * 1024;

    /**
     * Copy input stream to output stream, and close input
     */
    public static void copy(InputStream input, OutputStream output) {
        byte[] buffer = new byte[BUFFER_SIZE];
        try {
            int read;
            while ((read = input.read(buffer)) != -1) {
                output.write(buffer, 0, read);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } finally {
            InternalIOUtils.closeQuietly(input);
        }
    }

    /**
     * Read input to bytes, and then close input.
     *
     * @param initialSize the initial size to be used
     */
    public static byte[] readAll(InputStream input, int initialSize) {
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
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } finally {
            InternalIOUtils.closeQuietly(input);
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
    public static byte[] readAll(InputStream input) {
        return readAll(input, 32);
    }

    /**
     * Read exactly data with size.
     *
     * @return the size read; may less than len if reach the end for stream
     */
    public static int readExact(InputStream input, byte[] data, int offset, int len) {
        int read;
        int total = 0;
        try {
            while (len > 0 && (read = input.read(data, offset, len)) != -1) {
                total += read;
                offset += read;
                len -= read;
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return total;
    }

    /**
     * Skip all input data, and finally close it
     *
     * @return the num of bytes skipped
     */
    public static long skipAll(InputStream input) {
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
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } finally {
            InternalIOUtils.closeQuietly(input);
        }
        return count;
    }

    /**
     * Read reader to String, and then close reader
     */
    public static String readAll(Reader reader) {
        StringBuilder sb = new StringBuilder();
        char[] buffer = new char[BUFFER_SIZE];
        try {
            int read;
            while ((read = reader.read(buffer)) != -1) {
                sb.append(buffer, 0, read);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } finally {
            InternalIOUtils.closeQuietly(reader);
        }
        return sb.toString();
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
     * Close quietly, do not throw exceptions
     */
    public static void closeQuietly(@Nullable AutoCloseable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception ignore) {
            }
        }
    }
}
