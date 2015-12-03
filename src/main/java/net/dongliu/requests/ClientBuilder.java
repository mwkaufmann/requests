package net.dongliu.requests;

import net.dongliu.requests.struct.Host;
import net.dongliu.requests.struct.Pair;
import net.dongliu.requests.struct.Proxy;
import org.apache.http.HttpHost;
import org.apache.http.config.Registry;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author Liu Dong
 */
public class ClientBuilder {

    private int type = Client.CLIENT_TYPE_POOLED;

    // how long http connection keep, in milliseconds. default -1, get from server response
    private long timeToLive = -1;
    // the max total http connection count
    private int maxTotal = 20;
    // the max connection count for each host
    private int maxPerRoute = 5;
    // set max count for specified host
    private List<Pair<Host, Integer>> perRouteCount;

    private Proxy proxy;
    // settings for client level, can not set/override in request level
    // if verify http certificate
    private boolean verify = true;

    // if enable compress response
    private boolean compress = true;
    private boolean allowRedirects = true;
    private String userAgent = Utils.defaultUserAgent;

    ClientBuilder() {
    }

    public Client build() {
        Registry<ConnectionSocketFactory> registry = Utils.getConnectionSocketFactoryRegistry(proxy, verify);

        HttpClientConnectionManager connectionManager;
        if (type == Client.CLIENT_TYPE_POOLED) {
            PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager(registry,
                    null, null, null, timeToLive, TimeUnit.MILLISECONDS);

            manager.setMaxTotal(maxTotal);
            manager.setDefaultMaxPerRoute(maxPerRoute);
            if (perRouteCount != null) {
                for (Pair<Host, Integer> pair : perRouteCount) {
                    Host host = pair.getName();
                    manager.setMaxPerRoute(new HttpRoute(new HttpHost(host.getDomain(), host.getPort())),
                            pair.getValue());
                }
            }
            connectionManager = manager;
        } else if (type == Client.CLIENT_TYPE_BASIC) {
            connectionManager = new BasicHttpClientConnectionManager(registry);
        } else {
            throw new IllegalArgumentException("Unknown client type:" + type);
        }


        HttpClientBuilder clientBuilder = HttpClients.custom().setUserAgent(userAgent);
        clientBuilder.setConnectionManager(connectionManager);

        // disable compress
        if (!compress) {
            clientBuilder.disableContentCompression();
        }

        if (allowRedirects) {
            clientBuilder.setRedirectStrategy(new AllRedirectStrategy());
        } else {
            clientBuilder.disableRedirectHandling();
        }

        return new Client(clientBuilder.build(), proxy);
    }

    /**
     * how long http connection keep, in milliseconds. default -1, get from server response
     */
    public ClientBuilder timeToLive(long timeToLive) {
        this.timeToLive = timeToLive;
        return this;
    }

    /**
     * the max total http connection count. default 20
     */
    public ClientBuilder maxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
        return this;
    }

    /**
     * set default max connection count for each host, default 2
     */
    public ClientBuilder maxPerRoute(int maxPerRoute) {
        this.maxPerRoute = maxPerRoute;
        return this;
    }

    /**
     * set specified max connection count for the host, default 2
     */
    public ClientBuilder maxPerRoute(Host host, int maxPerRoute) {
        ensurePerRouteCount();
        this.perRouteCount.add(new Pair<>(host, maxPerRoute));
        return this;
    }

    /**
     * set userAgent
     */
    public ClientBuilder userAgent(String userAgent) {
        Objects.requireNonNull(userAgent);
        this.userAgent = userAgent;
        return this;
    }

    /**
     * if verify http certificate, default true
     */
    public ClientBuilder verify(boolean verify) {
        this.verify = verify;
        return this;
    }

    /**
     * If follow get/head redirect, default true.
     * This method not set following redirect for post/put/delete method, use {@code allowPostRedirects} if you want this
     */
    public ClientBuilder allowRedirects(boolean allowRedirects) {
        this.allowRedirects = allowRedirects;
        return this;
    }

    /**
     * if send compress requests. default true
     */
    public ClientBuilder compress(boolean compress) {
        this.compress = compress;
        return this;
    }

    private void ensurePerRouteCount() {
        if (this.perRouteCount == null) {
            this.perRouteCount = new ArrayList<>();
        }
    }

    public ClientBuilder type(int type) {
        this.type = type;
        return this;
    }

    public ClientBuilder proxy(Proxy proxy) {
        this.proxy = proxy;
        return this;
    }
}
