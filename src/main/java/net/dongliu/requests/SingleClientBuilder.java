package net.dongliu.requests;

import org.apache.http.config.Registry;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;

/**
 * un-thread-safe single connection client builder
 */
public class SingleClientBuilder extends ClientBuilder<SingleClientBuilder> {

    SingleClientBuilder() {
    }

    @Override
    protected SingleClientBuilder self() {
        return this;
    }

    @Override
    protected HttpClientConnectionManager buildManager(Registry<ConnectionSocketFactory> registry) {
        return new BasicHttpClientConnectionManager(registry);
    }
}
