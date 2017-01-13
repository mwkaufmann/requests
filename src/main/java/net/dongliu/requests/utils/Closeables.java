package net.dongliu.requests.utils;

import javax.annotation.Nullable;

/**
 * Utils method to close resource
 *
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
     * Close closeables, if multi exception occurred, add to suppressed list.
     *
     * @return the exception occurred
     */
    @Nullable
    public static Throwable close(AutoCloseable... closeables) {
        @Nullable Throwable e = null;
        for (AutoCloseable closeable : closeables) {
            if (closeable == null) {
                continue;
            }
            try {
                closeable.close();
            } catch (Throwable t) {
                if (e == null) {
                    e = t;
                } else {
                    e.addSuppressed(t);
                }
            }
        }
        return e;
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
