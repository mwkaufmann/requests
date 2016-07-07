package net.dongliu.requests.exception;

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
    public static RuntimeException sneakyThrow(Throwable throwable) {
        Exceptions.magicThrow(throwable);
        return null;
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> void magicThrow(Throwable throwable) throws T {
        throw (T) throwable;
    }

    /**
     * Write exception stack trace to String
     */
    public static String toString(Throwable t) {
        try (StringWriter sw = new StringWriter();
             PrintWriter pw = new PrintWriter(sw)) {
            t.printStackTrace(pw);
            return sw.toString();
        } catch (IOException e) {
            throw Exceptions.sneakyThrow(e);
        }
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
