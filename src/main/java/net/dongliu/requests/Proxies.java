package net.dongliu.requests;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Objects;

/**
 * Utils class for create Proxy
 *
 * @author Liu Dong
 */
public class Proxies {
    /**
     * Create http proxy
     */
    public static Proxy httpProxy(String host, int port) {
        return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(Objects.requireNonNull(host), port));
    }

    /**
     * Create socks5 proxy
     */
    public static Proxy socksProxy(String host, int port) {
        return new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(Objects.requireNonNull(host), port));
    }
}
