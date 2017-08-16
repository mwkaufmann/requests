package net.dongliu.requests;

import javax.annotation.Nullable;
import java.net.Proxy;
import java.util.List;

/**
 * The Mixin Http Request builder, bind ability of BaseRequestBuilder and ClientBuilder, using for Requests util methods.
 * This class use the name for compatible reason.
 *
 * @author Liu Dong
 */
public final class RequestBuilder extends BaseRequestBuilder<RequestBuilder> {
    private ClientBuilder clientBuilder = new ClientBuilder();

    RequestBuilder() {
        super();
    }

    RequestBuilder(Request request) {
        super(request);
    }

    /**
     * Set tcp socks timeout in mills
     */
    public RequestBuilder socksTimeout(int timeout) {
        clientBuilder.socksTimeout(timeout);
        return this;
    }

    /**
     * Set tcp connect timeout in mills
     */
    public RequestBuilder connectTimeout(int timeout) {
        clientBuilder.connectTimeout(timeout);
        return this;
    }

    /**
     * Set connect timeout and socket time out
     */
    public RequestBuilder timeout(int timeout) {
        clientBuilder.timeout(timeout);
        return this;
    }

    /**
     * set proxy
     */
    public RequestBuilder proxy(@Nullable Proxy proxy) {
        clientBuilder.proxy(proxy);
        return this;
    }

    /**
     * Set auto handle redirect. default true
     */
    public RequestBuilder followRedirect(boolean followRedirect) {
        clientBuilder.followRedirect(followRedirect);
        return this;
    }

    /**
     * Set accept compressed response. default true
     */
    public RequestBuilder compress(boolean compress) {
        clientBuilder.compress(compress);
        return this;
    }

    /**
     * Check ssl cert. default true
     */
    public RequestBuilder verify(boolean verify) {
        clientBuilder.verify(verify);
        return this;
    }

    /**
     * If reuse http connection. default true
     */
    public RequestBuilder keepAlive(boolean keepAlive) {
        clientBuilder.keepAlive(keepAlive);
        return this;
    }

    /**
     * Add trust certs
     */
    public RequestBuilder certs(List<CertificateInfo> certs) {
        clientBuilder.certs(certs);
        return this;
    }


    /**
     * build http request, create http client if needed, and send out
     */
    @Override
    public RawResponse send() {
        Request request = build();
        ClientPool instance = ClientPool.getInstance();
        Client client = instance.getClient(clientBuilder);
        return client.execute(request);
    }


}