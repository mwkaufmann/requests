package net.dongliu.requests;

import net.dongliu.requests.struct.Method;
import net.dongliu.requests.struct.Proxy;

import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Map;

/**
 * Common logic for Mixed client builder and request builder
 */
public abstract class AbstractMixinRequestBuilder<T extends AbstractMixinRequestBuilder<T, V>, V extends RequestBuilder<V>>
        implements IClientBuilder<T>, IBaseRequestBuilder<T> {
    private final SingleClientBuilder singleClientBuilder;

    AbstractMixinRequestBuilder() {
        this.singleClientBuilder = new SingleClientBuilder().closeOnRequestFinished(true);
    }

    protected abstract T self();

    protected abstract RequestBuilder<V> requestBuilder();

    //----------------- client builder --------------------------------

    public T connectTimeout(int timeout) {
        singleClientBuilder.connectTimeout(timeout);
        return self();
    }

    public T timeToLive(long timeToLive) {
        singleClientBuilder.timeToLive(timeToLive);
        return self();
    }

    public T userAgent(String userAgent) {
        singleClientBuilder.userAgent(userAgent);
        return self();
    }

    public T verify(boolean verify) {
        singleClientBuilder.verify(verify);
        return self();
    }

    public T allowRedirects(boolean allowRedirects) {
        singleClientBuilder.allowRedirects(allowRedirects);
        return self();
    }

    public T compress(boolean compress) {
        singleClientBuilder.compress(compress);
        return self();
    }

    public T proxy(Proxy proxy) {
        singleClientBuilder.proxy(proxy);
        return self();
    }

    public T timeout(int timeout) {
        singleClientBuilder.timeout(timeout);
        return self();
    }

    public T socketTimeout(int timeout) {
        singleClientBuilder.socketTimeout(timeout);
        return self();
    }


    //---------------------  for request builder --------------------

    private RequestBuilder<V> finalRequestBuilder() {
        return requestBuilder().client(singleClientBuilder.build());
    }

    public RawResponse send() throws UncheckedIOException {
        return finalRequestBuilder().send();
    }

    public T url(String url) throws UncheckedIOException {
        requestBuilder().url(url);
        return self();
    }

    public T requestCharset(Charset charset) {
        requestBuilder().requestCharset(charset);
        return self();
    }

    public T method(Method method) {
        requestBuilder().method(method);
        return self();
    }

    public T session(Session session) {
        requestBuilder().session(session);
        return self();
    }

    @Override
    public T params(Collection<? extends Map.Entry<String, String>> params) {
        requestBuilder().params(params);
        return self();
    }

    @Override
    public T headers(Collection<? extends Map.Entry<String, String>> headers) {
        requestBuilder().headers(headers);
        return self();
    }

    @Override
    public T basicAuth(String userName, String password) {
        requestBuilder().basicAuth(userName, password);
        return self();
    }

    @Override
    public T cookies(Collection<? extends Map.Entry<String, String>> cookies) {
        requestBuilder().cookies(cookies);
        return self();
    }
}
