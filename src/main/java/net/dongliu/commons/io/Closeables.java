package net.dongliu.commons.io;

import net.dongliu.commons.exception.Exceptions;

import javax.annotation.Nullable;

/**
 * @author Liu Dong
 */
public class Closeables {

    /**
     * Close, if exception occurred, add to suppressed exception.
     * The exception passed in will not be thrown
     */
    public static void close(@Nullable AutoCloseable closeable, Throwable t) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                t.addSuppressed(e);
            }
        }
    }

    /**
     * Close, if exception occurred, sneaky throw the exception
     */
    public static void close(@Nullable AutoCloseable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                throw Exceptions.sneakyThrow(e);
            }
        }
    }

    /**
     * Close closeables, if exception occurred, sneaky throw the exception
     */
    public static void close(AutoCloseable... closeables) {
        Throwable e = null;
        for (AutoCloseable closeable : closeables) {
            if (closeable == null) {
                continue;
            }
            try {
                closeable.close();
            } catch (Throwable t) {
                if (e != null) {
                    e.addSuppressed(t);
                } else {
                    e = t;
                }
            }
        }
        if (e != null) {
            throw Exceptions.sneakyThrow(e);
        }
    }


    /**
     * Close closeables, if exception occurred, add to suppressed list.
     * The exception passed in will not be thrown
     */
    public static void close(Throwable e, AutoCloseable... closeables) {
        for (AutoCloseable closeable : closeables) {
            if (closeable == null) {
                continue;
            }
            try {
                closeable.close();
            } catch (Throwable t) {
                e.addSuppressed(t);
            }
        }
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

    /**
     * Close multi closable instance quietly, do not throw exceptions
     */
    public static void closeQuietly(AutoCloseable... closeables) {
        for (AutoCloseable closeable : closeables) {
            closeQuietly(closeable);
        }
    }
}
