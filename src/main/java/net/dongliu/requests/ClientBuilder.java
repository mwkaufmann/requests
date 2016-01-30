package net.dongliu.requests;

import net.dongliu.requests.struct.Proxy;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Objects;

/**
 * @author Liu Dong
 */
public abstract class ClientBuilder<T extends ClientBuilder<T>> implements ClientBuilderInterface<T> {

    final static String defaultUserAgent = "Requests/2.1.5, Java " + System.getProperty("java.version");
    final static int defaultTimeout = 10_000;

    // how long http connection keep, in milliseconds. default -1, get from server response
    protected long timeToLive = -1;

    private Proxy proxy;
    // settings for client level, can not set/override in request level
    // if verify http certificate
    private boolean verify = true;

    // if enable compress response
    private boolean compress = true;
    private boolean allowRedirects = true;
    private String userAgent = defaultUserAgent;

    private int connectTimeout = defaultTimeout;
    private int socketTimeout = defaultTimeout;

    private boolean closeOnRequstFinished = false;

    ClientBuilder() {
    }

    public Client build() {
        Registry<ConnectionSocketFactory> registry = getConnectionSocketFactoryRegistry(proxy, verify);
        HttpClientConnectionManager connectionManager = buildManager(registry);

        HttpClientBuilder clientBuilder = HttpClients.custom().setUserAgent(userAgent);
        clientBuilder.setConnectionManager(connectionManager);

        RequestConfig.Builder configBuilder = RequestConfig.custom()
                .setConnectTimeout(connectTimeout)
                .setSocketTimeout(socketTimeout)
                // we use connect timeout for connection request timeout
                .setConnectionRequestTimeout(connectTimeout)
                .setCookieSpec(CookieSpecs.DEFAULT);
        clientBuilder.setDefaultRequestConfig(configBuilder.build());

        // disable compress
        if (!compress) {
            clientBuilder.disableContentCompression();
        }

        if (allowRedirects) {
            clientBuilder.setRedirectStrategy(new AllRedirectStrategy());
        } else {
            clientBuilder.disableRedirectHandling();
        }

        return new Client(clientBuilder.build(), closeOnRequstFinished);
    }


    protected Registry<ConnectionSocketFactory> getConnectionSocketFactoryRegistry(Proxy proxy, boolean verify) {
        SSLContext sslContext;

        // trust all http certificate
        if (!verify) {
            try {
                sslContext = SSLContexts.custom().useTLS().build();
                sslContext.init(new KeyManager[0], new TrustManager[]{new AllTrustManager()},
                        new SecureRandom());
            } catch (NoSuchAlgorithmException | KeyManagementException e) {
                throw new RuntimeException(e);
            }
        } else {
            sslContext = SSLContexts.createSystemDefault();
        }

        SSLConnectionSocketFactory sslsf = new CustomSSLConnectionSocketFactory(sslContext, proxy, verify);
        PlainConnectionSocketFactory psf = new CustomConnectionSocketFactory(proxy);
        return RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", psf)
                .register("https", sslsf)
                .build();
    }

    @Override
    public T timeToLive(long timeToLive) {
        this.timeToLive = timeToLive;
        return self();
    }

    @Override
    public T userAgent(String userAgent) {
        Objects.requireNonNull(userAgent);
        this.userAgent = userAgent;
        return self();
    }

    @Override
    public T verify(boolean verify) {
        this.verify = verify;
        return self();
    }

    @Override
    public T allowRedirects(boolean allowRedirects) {
        this.allowRedirects = allowRedirects;
        return self();
    }

    @Override
    public T compress(boolean compress) {
        this.compress = compress;
        return self();
    }

    @Override
    public T proxy(Proxy proxy) {
        this.proxy = proxy;
        return self();
    }


    @Override
    public T timeout(int timeout) {
        this.connectTimeout = this.socketTimeout = timeout;
        return self();
    }

    @Override
    public T socketTimeout(int timeout) {
        this.socketTimeout = timeout;
        return self();
    }

    @Override
    public T connectTimeout(int timeout) {
        this.connectTimeout = timeout;
        return self();
    }

    /**
     * Auto close client when finished one request. default false.
     * Only for internal use
     */
    T closeOnRequstFinished(boolean closeOnRequstFinished) {
        this.closeOnRequstFinished = closeOnRequstFinished;
        return self();
    }

    protected abstract T self();

    protected abstract HttpClientConnectionManager buildManager(Registry<ConnectionSocketFactory> registry);

}
