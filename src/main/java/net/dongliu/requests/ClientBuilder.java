package net.dongliu.requests;

import net.dongliu.requests.executor.RequestProvider;
import net.dongliu.requests.executor.RequestProviders;
import net.dongliu.requests.utils.Predicates;

import javax.annotation.Nullable;
import java.net.Proxy;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Builder to crate http Client.
 */
public class ClientBuilder {
    // connection setting. Those setting can not be changed by request.
    int maxConnection = DefaultSettings.MAX_CONNECTION;
    int maxConnectionPerHost = DefaultSettings.MAX_CONNECTION_PER_HOST;
    int socksTimeout = DefaultSettings.SOCKS_TIMEOUT;
    int connectTimeout = DefaultSettings.CONNECT_TIMEOUT;
    @Nullable
    Proxy proxy;
    boolean verify = true;
    List<CertificateInfo> certs = Collections.emptyList();
    boolean keepAlive = true;

    // http processor setting
    boolean followRedirect = true;
    boolean compress = true;

    //-----  the following setting can be override by request property.
    String userAgent = DefaultSettings.USER_AGENT;

    ClientBuilder() {
    }

    /**
     * Build a http client use settings.
     */
    public Client create() {
        RequestProvider provider = RequestProviders.lookup();
        return provider.newClient(this);
    }

    /**
     * Max connection can keep by connection pool of this client.
     * This setting do not work for URLConnection impl.
     */
    public ClientBuilder maxConnection(int maxConnection) {
        this.maxConnection = Predicates.bigThanZero(maxConnection);
        return this;
    }

    /**
     * Max connection can keep by connection pool of this client.
     * This setting do not work for URLConnection impl.
     */
    public ClientBuilder maxConnectionPerHost(int maxConnectionPerHost) {
        this.maxConnectionPerHost = Predicates.bigThanZero(maxConnectionPerHost);
        return this;
    }

    /**
     * Set socket read/write timeout in millis
     */
    public ClientBuilder socksTimeout(int timeout) {
        this.socksTimeout = Predicates.bigThanZero(timeout);
        return this;
    }

    /**
     * Set socket connect timeout in millis
     */
    public ClientBuilder connectTimeout(int timeout) {
        this.connectTimeout = Predicates.bigThanZero(timeout);
        return this;
    }

    /**
     * Set socket connect and read timeout in millis
     */
    public ClientBuilder timeout(int timeout) {
        this.connectTimeout = Predicates.bigThanZero(timeout);
        this.socksTimeout = timeout;
        return this;
    }

    /**
     * Set proxy this client use.
     */
    public ClientBuilder proxy(@Nullable Proxy proxy) {
        this.proxy = proxy;
        return this;
    }

    /**
     * If check https certificate. Default true
     */
    public ClientBuilder verify(boolean verify) {
        this.verify = verify;
        return this;
    }

    /**
     * Add custom http certificate to client.
     */
    public ClientBuilder certs(List<CertificateInfo> certs) {
        this.certs = Objects.requireNonNull(certs);
        return this;
    }

    /**
     * If auto follow http redirect. Default true
     */
    public ClientBuilder followRedirect(boolean followRedirect) {
        this.followRedirect = followRedirect;
        return this;
    }

    /**
     * If send gzip/deflate.... header in http request and handle http response decompress. Default true.
     */
    public ClientBuilder compress(boolean compress) {
        this.compress = compress;
        return this;
    }

    /**
     * If reuse http connection. Default is true.
     */
    public ClientBuilder keepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
        return this;
    }

    /**
     * The http use agent.
     */
    public ClientBuilder userAgent(String userAgent) {
        this.userAgent = Objects.requireNonNull(userAgent);
        return this;
    }
}
