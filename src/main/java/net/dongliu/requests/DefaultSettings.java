package net.dongliu.requests;

public class DefaultSettings {

    /**
     * Default user agent
     */
    public static final String USER_AGENT = "Requests 4.12.0, Java " + System.getProperty("java.version");

    /**
     * Default connect timeout for http connection
     */
    public static final int CONNECT_TIMEOUT = 1000;
    /**
     * Default socks timeout for http connection
     */
    public static final int SOCKS_TIMEOUT = 10_000;

    /**
     * Default max connection count for one http client
     */
    public static final int MAX_CONNECTION = 20;

    /**
     * Default max connection count per host value for http client
     */
    public static final int MAX_CONNECTION_PER_HOST = 10;
}
