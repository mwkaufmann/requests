package net.dongliu.requests;

import net.dongliu.requests.exception.RequestException;
import net.dongliu.requests.struct.Host;
import net.dongliu.requests.struct.Pair;
import net.dongliu.requests.struct.Proxy;
import org.apache.http.HttpHost;
import org.apache.http.config.Registry;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * http connection pool. use this to reuse http connection across http requests.
 * This class is thread-safe, can service connection requests from multiple execution threads.
 *
 * @author Dong Liu dongliu@wandoujia.com
 */
public class ConnectionPool implements Closeable {

    private final PoolingHttpClientConnectionManager manager;

    public ConnectionPool(PoolingHttpClientConnectionManager manager, Proxy proxy) {
        this.manager = manager;
        this.proxy = proxy;
    }

    /**
     * create new ConnectionPool Builder.
     */
    public static ConnectionPoolBuilder custom() {
        return new ConnectionPoolBuilder();
    }

    private final Proxy proxy;

    @Override
    public void close() throws IOException {
        manager.close();
    }

    Proxy getProxy() {
        return proxy;
    }

    HttpClientConnectionManager wrappedConnectionManager() {
        return new ConnectionManagerWrapper(manager);
    }

    /**
     * get method
     */
    public RequestBuilder get(String url) throws RequestException {
        return Requests.get(url).connectionPool(this);
    }

    /**
     * head method
     */
    public RequestBuilder head(String url) throws RequestException {
        return Requests.head(url).connectionPool(this);
    }

    /**
     * get url, and return content
     */
    public RequestBuilder post(String url) throws RequestException {
        return Requests.post(url).connectionPool(this);
    }

    /**
     * put method
     */
    public RequestBuilder put(String url) throws RequestException {
        return Requests.put(url).connectionPool(this);
    }

    /**
     * delete method
     */
    public RequestBuilder delete(String url) throws RequestException {
        return Requests.delete(url).connectionPool(this);
    }

    /**
     * options method
     */
    public RequestBuilder options(String url) throws RequestException {
        return Requests.options(url).connectionPool(this);
    }

    /**
     * patch method
     */
    public RequestBuilder patch(String url) throws RequestException {
        return Requests.patch(url).connectionPool(this);
    }

    /**
     * create a session. session can do request as Requests do, and keep cookies to maintain a http session
     */
    public Session session() {
        return new Session(this);
    }

    /**
     * trace method
     */
    public RequestBuilder trace(String url) throws RequestException {
        return Requests.trace(url).connectionPool(this);
    }

    public static class ConnectionPoolBuilder {
        // how long http connection keep, in milliseconds. default -1, get from server response
        private long timeToLive = -1;
        // the max total http connection count
        private int maxTotal = 20;
        // the max connection count for each host
        private int maxPerRoute = 2;
        // set max count for specified host
        private List<Pair<Host, Integer>> perRouteCount;
        // if verify http certificate
        private boolean verify = true;
        private Proxy proxy;

        ConnectionPoolBuilder() {
        }

        public ConnectionPool build() {
            Registry<ConnectionSocketFactory> r = Utils.getConnectionSocketFactoryRegistry(proxy,
                    verify);
            PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager(r,
                    null, null, null, timeToLive, TimeUnit.MILLISECONDS);

            manager.setMaxTotal(maxTotal);
            manager.setDefaultMaxPerRoute(maxPerRoute);
            if (perRouteCount != null) {
                for (Pair<Host, Integer> pair : perRouteCount) {
                    Host host = pair.getName();
                    manager.setMaxPerRoute(
                            new HttpRoute(new HttpHost(host.getDomain(), host.getPort())),
                            pair.getValue());
                }
            }
            return new ConnectionPool(manager, proxy);
        }


        /**
         * how long http connection keep, in milliseconds. default -1, get from server response
         */
        public ConnectionPoolBuilder timeToLive(long timeToLive) {
            this.timeToLive = timeToLive;
            return this;
        }

        /**
         * the max total http connection count. default 20
         */
        public ConnectionPoolBuilder maxTotal(int maxTotal) {
            this.maxTotal = maxTotal;
            return this;
        }

        /**
         * set default max connection count for each host, default 2
         */
        public ConnectionPoolBuilder maxPerRoute(int maxPerRoute) {
            this.maxPerRoute = maxPerRoute;
            return this;
        }

        /**
         * set specified max connection count for the host, default 2
         */
        public ConnectionPoolBuilder maxPerRoute(Host host, int maxPerRoute) {
            ensurePerRouteCount();
            this.perRouteCount.add(new Pair<>(host, maxPerRoute));
            return this;
        }

        /**
         * if verify http certificate, default true
         */
        public ConnectionPoolBuilder verify(boolean verify) {
            this.verify = verify;
            return this;
        }

        private void ensurePerRouteCount() {
            if (this.perRouteCount == null) {
                this.perRouteCount = new ArrayList<>();
            }
        }

        private ConnectionPoolBuilder proxy(Proxy proxy) {
            this.proxy = proxy;
            return this;
        }
    }
}
