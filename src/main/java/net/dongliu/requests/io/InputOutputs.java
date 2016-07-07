package net.dongliu.requests.io;

import net.dongliu.requests.exception.Exceptions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * Utils method for io
 *
 * @author Liu Dong
 */
public class InputOutputs {

    private static final int BUFFER_SIZE = 8 * 1024;

    /**
     * Copy input stream to ouput stream, and close input
     */
    public static void copy(InputStream input, OutputStream output) {
        byte[] buffer = new byte[BUFFER_SIZE];
        try {
            int read;
            while ((read = input.read(buffer)) != -1) {
                output.write(buffer, 0, read);
            }
        } catch (IOException e) {
            throw Exceptions.sneakyThrow(e);
        } finally {
            Closeables.closeQuietly(input);
        }
    }

    /**
     * Read input to bytes, and then close input
     */
    public static byte[] readAll(InputStream input) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            copy(input, bos);
            return bos.toByteArray();
        } catch (IOException e) {
            throw Exceptions.sneakyThrow(e);
        }
    }

    /**
     * Read input to bytes with known size, and then close input
     *
     * @throws ArrayIndexOutOfBoundsException
     */
    public static byte[] readAll(InputStream input, int maxSize) {
        byte[] data = new byte[maxSize];
        int offset = 0;
        int read;
        try {
            while ((read = input.read(data, offset, BUFFER_SIZE)) != -1) {
                offset += read;
            }
            if (offset < maxSize) {
                return Arrays.copyOf(data, offset);
            }
            return data;
        } catch (IOException e) {
            throw Exceptions.sneakyThrow(e);
        } finally {
            Closeables.closeQuietly(input);
        }
    }

    /**
     * Read input to bytes with known size,into byte array data
     *
     * @throws ArrayIndexOutOfBoundsException
     */
    public static int readAll(InputStream input, byte[] data) {
        return readAll(input, data, 0);
    }

    /**
     * Read input to bytes with known size,into byte array data
     *
     * @throws ArrayIndexOutOfBoundsException
     */
    public static int readAll(InputStream input, byte[] data, int offset) {
        int woffset = offset;
        int read;
        try {
            while ((read = input.read(data, woffset, BUFFER_SIZE)) != -1) {
                woffset += read;
            }
            return woffset - offset;
        } catch (IOException e) {
            throw Exceptions.sneakyThrow(e);
        } finally {
            Closeables.closeQuietly(input);
        }
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
            while ((read = input.skip(BUFFER_SIZE)) != 0) {
                count += read;
            }
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((read = input.read(buffer)) != -1) {
                count += read;
            }
        } catch (IOException e) {
            throw Exceptions.sneakyThrow(e);
        } finally {
            Closeables.closeQuietly(input);
        }
        return count;
    }
}
