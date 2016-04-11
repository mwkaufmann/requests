package net.dongliu.requests;

import net.dongliu.requests.struct.Proxy;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.protocol.HttpContext;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @author Dong Liu dongliu@live.cn
 */
class CustomConnectionSocketFactory extends PlainConnectionSocketFactory {
    @Nullable
    private final Proxy proxy;

    public CustomConnectionSocketFactory(@Nullable Proxy proxy) {
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
