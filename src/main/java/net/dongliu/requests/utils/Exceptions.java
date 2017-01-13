package net.dongliu.requests.utils;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Utils for exception
 *
 * @author Liu Dong {@literal <dongliu@live.cn>}
 */
public class Exceptions {

    /**
     * Throw a exception with no need to declare throw clause in method signature
     */
    @Nonnull
    public static RuntimeException sneakyThrow(Throwable throwable) {
        Exceptions._sneakyThrow(throwable);
        return null;
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> void _sneakyThrow(Throwable throwable) throws T {
        throw (T) throwable;
    }

    /**
     * Write exception stack trace to string
     */
    public static String writeToString(Throwable e) {
        try (StringWriter sw = new StringWriter(256);
             PrintWriter pw = new PrintWriter(sw)) {
            e.printStackTrace(pw);
            return sw.toString();
        } catch (IOException e1) {
            //should not happen
            throw Exceptions.sneakyThrow(e1);
        }
    }
}
