package net.dongliu.requests;


import net.dongliu.requests.struct.Host;
import org.apache.http.HttpHost;
import org.apache.http.config.Registry;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PooledClientBuilder extends ClientBuilder<PooledClientBuilder> {
    // the max total http connection count
    private int maxTotal = 20;
    // the max connection count for each host
    private int maxPerRoute = 5;
    // set max count for specified host
    private Map<Host, Integer> perRouteCount;

    PooledClientBuilder() {
    }

    /**
     * the max total http connection count. default 20
     */
    public PooledClientBuilder maxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
        return this;
    }

    /**
     * set default max connection count for each host, default 2
     */
    public PooledClientBuilder maxPerRoute(int maxPerRoute) {
        this.maxPerRoute = maxPerRoute;
        return this;
    }

    /**
     * set specified max connection count for the host, default 2
     */
    public PooledClientBuilder maxPerRoute(Host host, int maxPerRoute) {
        ensurePerRouteCount();
        this.perRouteCount.put(host, maxPerRoute);
        return this;
    }

    private void ensurePerRouteCount() {
        if (this.perRouteCount == null) {
            this.perRouteCount = new HashMap<>();
        }
    }

    @Override
    protected PooledClientBuilder self() {
        return this;
    }

    @Override
    protected HttpClientConnectionManager buildManager(Registry<ConnectionSocketFactory> registry) {
        PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager(registry,
                null, null, null, timeToLive, TimeUnit.MILLISECONDS);

        manager.setMaxTotal(maxTotal);
        manager.setDefaultMaxPerRoute(maxPerRoute);
        if (perRouteCount != null) {
            for (Map.Entry<Host, Integer> pair : perRouteCount.entrySet()) {
                Host host = pair.getKey();
                manager.setMaxPerRoute(new HttpRoute(new HttpHost(host.getDomain(), host.getPort())),
                        pair.getValue());
            }
        }
        return manager;
    }
}
