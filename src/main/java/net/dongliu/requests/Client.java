package net.dongliu.requests;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.net.Proxy;
import java.net.URL;
import java.util.List;

/**
 * Http Client, contains connection settings, processors, etc. You need close the client when no longer used.
 */
@ThreadSafe
public abstract class Client implements AutoCloseable {
    // connection setting
    private final int maxConnection;
    private final int maxConnectionPerHost;
    private final int socksTimeout;
    private final int connectTimeout;
    @Nullable
    private final Proxy proxy;
    private final boolean verify;
    private final List<CertificateInfo> certs;

    // http processor setting
    private final boolean followRedirect;
    private final boolean compress;
    private final boolean keepAlive;

    // client setting
    private final String userAgent;

    protected Client(ClientBuilder builder) {
        this.maxConnection = builder.maxConnection;
        this.maxConnectionPerHost = builder.maxConnectionPerHost;
        this.socksTimeout = builder.socksTimeout;
        this.connectTimeout = builder.connectTimeout;
        this.proxy = builder.proxy;
        this.verify = builder.verify;
        this.certs = builder.certs;
        this.followRedirect = builder.followRedirect;
        this.compress = builder.compress;
        this.keepAlive = builder.keepAlive;
        this.userAgent = builder.userAgent;
    }

    /**
     * Create new client builder
     */
    public static ClientBuilder Builder() {
        return new ClientBuilder();
    }

    /**
     * Create new http client with default settings.
     */
    public static Client newClient() {
        return Builder().create();
    }

    /**
     * Process the request, and return response
     */
    @Nonnull
    protected abstract RawResponse execute(Request request);

    /**
     * Create new request with method and url, would be executed bu this client.
     */
    public PlainRequestBuilder newRequest(String method, String url) {
        return new PlainRequestBuilder(this).method(method).url(url).userAgent(userAgent);
    }

    /**
     * Create new request with method and url, would be executed bu this client.
     */
    public PlainRequestBuilder newRequest(String method, URL url) {
        return new PlainRequestBuilder(this).method(method).url(url).userAgent(userAgent);
    }

    /**
     * Start a GET request using this http client.
     */
    public PlainRequestBuilder get(String url) {
        return newRequest(Methods.GET, url);
    }

    /**
     * Start a POST request using this http client.
     */
    public PlainRequestBuilder post(String url) {
        return newRequest(Methods.POST, url);
    }

    /**
     * Start a PUT request using this http client.
     */
    public PlainRequestBuilder put(String url) {
        return newRequest(Methods.PUT, url);
    }

    /**
     * Start a DELETE request using this http client.
     */
    public PlainRequestBuilder delete(String url) {
        return newRequest(Methods.DELETE, url);
    }

    /**
     * Start a HEAD request using this http client.
     */
    public PlainRequestBuilder head(String url) {
        return newRequest(Methods.HEAD, url);
    }

    /**
     * Start a PATCH request using this http client.
     */
    public PlainRequestBuilder patch(String url) {
        return newRequest(Methods.PATCH, url);
    }

    /**
     * Start a GET request using this http client.
     */
    public PlainRequestBuilder get(URL url) {
        return newRequest(Methods.GET, url);
    }

    /**
     * Start a POST request using this http client.
     */
    public PlainRequestBuilder post(URL url) {
        return newRequest(Methods.POST, url);
    }

    /**
     * Start a PUT request using this http client.
     */
    public PlainRequestBuilder put(URL url) {
        return newRequest(Methods.PUT, url);
    }

    /**
     * Start a DELETE request using this http client.
     */
    public PlainRequestBuilder delete(URL url) {
        return newRequest(Methods.DELETE, url);
    }

    /**
     * Start a HEAD request using this http client.
     */
    public PlainRequestBuilder head(URL url) {
        return newRequest(Methods.HEAD, url);
    }

    /**
     * Start a PATCH request using this http client.
     */
    public PlainRequestBuilder patch(URL url) {
        return newRequest(Methods.PATCH, url);
    }

    /**
     * Close this http client.
     */
    @Override
    public abstract void close() throws Exception;

    public int getMaxConnection() {
        return maxConnection;
    }

    public int getMaxConnectionPerHost() {
        return maxConnectionPerHost;
    }

    public int getSocksTimeout() {
        return socksTimeout;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    @Nullable
    public Proxy getProxy() {
        return proxy;
    }

    public boolean isVerify() {
        return verify;
    }

    public List<CertificateInfo> getCerts() {
        return certs;
    }

    public boolean isFollowRedirect() {
        return followRedirect;
    }

    public boolean isCompress() {
        return compress;
    }

    public boolean isKeepAlive() {
        return keepAlive;
    }

    public String getUserAgent() {
        return userAgent;
    }

}
