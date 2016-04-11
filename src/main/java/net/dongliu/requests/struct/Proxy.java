package net.dongliu.requests.struct;

import org.apache.http.annotation.Immutable;

import javax.annotation.Nullable;

/**
 * http proxy / socket proxy
 *
 * @author Dong Liu
 */
@Immutable
public class Proxy {
    private final String scheme;
    private final String host;
    private final int port;
    @Nullable
    private final AuthInfo authInfo;

    public static final String HTTP = "http";
    public static final String HTTPS = "https";
    public static final String SOCKS = "socks";

    private Proxy(String scheme, String host, int port, @Nullable AuthInfo authInfo) {
        this.scheme = scheme;
        this.host = host;
        this.port = port;
        this.authInfo = authInfo;
    }

    public static Proxy httpProxy(String host, int port) {
        return new Proxy(HTTP, host, port, null);
    }

    //TODO: java can only set global socks proxy setting
//    public static Proxy socksProxy(String host, int port, String userName, String password) {
//        return new Proxy(SOCKS, host, port, new AuthInfo(userName, password));
//    }

    public static Proxy httpProxy(String host, int port, String userName, String password) {
        return new Proxy(HTTP, host, port, new AuthInfo(userName, password));
    }

    // TODO: https proxy not supported?
//    public static Proxy httpsProxy(String host, int port) {
//        return new Proxy(HTTPS, host, port, null);
//    }

//    public static Proxy httpsProxy(String host, int port, String userName, String password) {
//        return new Proxy(HTTPS, host, port, new AuthInfo(userName, password));
//    }


    public static Proxy socksProxy(String host, int port) {
        return new Proxy(SOCKS, host, port, null);
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    @Nullable
    public AuthInfo getAuthInfo() {
        return authInfo;
    }

    public String getScheme() {
        return scheme;
    }

}
