package net.dongliu.requests;

import net.dongliu.requests.struct.Proxy;

public interface IClientBuilder<T> {
    /**
     * how long http connection keep, in milliseconds. default -1, get from server response
     */
    T timeToLive(long timeToLive);

    /**
     * set userAgent
     */
    T userAgent(String userAgent);

    /**
     * if verify http certificate, default true
     */
    T verify(boolean verify);

    /**
     * If follow get/head redirect, default true.
     * This method not set following redirect for post/put/delete method, use {@code allowPostRedirects} if you want this
     */
    T allowRedirects(boolean allowRedirects);

    /**
     * if send compress requests. default true
     */
    T compress(boolean compress);

    /**
     * Set connection proxy
     */
    T proxy(Proxy proxy);

    /**
     * Set socket timeout and connect timeout in millis, default 10_000
     */
    T timeout(int timeout);

    /**
     * Set socket timeout in millis, default 10_000
     */
    T socketTimeout(int timeout);

    /**
     * Set connect timeout in millis, default 10_000
     */
    T connectTimeout(int timeout);
}
