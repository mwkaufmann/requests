package net.dongliu.requests;

import net.dongliu.requests.struct.Proxy;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.protocol.HttpContext;

import javax.annotation.Nullable;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @author Dong Liu dongliu@live.cn
 */
class CustomSSLConnectionSocketFactory extends SSLConnectionSocketFactory {
    @Nullable
    private final Proxy proxy;

    public CustomSSLConnectionSocketFactory(SSLContext sslContext, @Nullable Proxy proxy, boolean verify) {
        super(sslContext, verify ? SSLConnectionSocketFactory.getDefaultHostnameVerifier()
                : NoopHostnameVerifier.INSTANCE);
        this.proxy = proxy;
    }

    @Override
    public Socket createSocket(final HttpContext context) throws IOException {
        if (proxy == null || !proxy.getScheme().equals(Proxy.SOCKS)) {
            return super.createSocket(context);
        }
        java.net.Proxy proxy = new java.net.Proxy(java.net.Proxy.Type.SOCKS,
                new InetSocketAddress(this.proxy.getHost(), this.proxy.getPort()));
        return new Socket(proxy);
    }

}
