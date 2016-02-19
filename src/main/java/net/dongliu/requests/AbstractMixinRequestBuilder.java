package net.dongliu.requests;

import net.dongliu.requests.exception.RequestException;
import net.dongliu.requests.struct.*;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Common logic for Mixed client builder and request builder
 */
public abstract class AbstractMixinRequestBuilder<T extends AbstractMixinRequestBuilder<T, V>, V extends RequestBuilder<V>>
        implements ClientBuilderInterface<T>, Executable, BaseRequestBuilderInterface<T> {
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

    public <R> Response<R> execute(ResponseProcessor<R> processor) throws RequestException {
        return finalRequestBuilder().execute(processor);
    }

    public <R> Response<R> handle(ResponseHandler<R> handler) throws RequestException {
        return finalRequestBuilder().handle(handler);
    }

    public Response<String> text(Charset responseCharset) throws RequestException {
        return finalRequestBuilder().text(responseCharset);
    }

    public Response<String> text() throws RequestException {
        return finalRequestBuilder().text();
    }

    public Response<byte[]> bytes() throws RequestException {
        return finalRequestBuilder().bytes();
    }

    public Response<File> file(File file) throws RequestException {
        return finalRequestBuilder().file(file);
    }

    public Response<File> file(String filePath) throws RequestException {
        return finalRequestBuilder().file(filePath);
    }

    public T url(String url) throws RequestException {
        requestBuilder().url(url);
        return self();
    }

    public T params(Map<String, ?> params) {
        requestBuilder().params(params);
        return self();
    }

    public T params(Parameter... params) {
        requestBuilder().params(params);
        return self();
    }

    public T params(Collection<Parameter> params) {
        requestBuilder().params(params);
        return self();
    }

    public T addParam(String key, Object value) {
        requestBuilder().addParam(key, value);
        return self();
    }

    public T charset(Charset charset) {
        requestBuilder().charset(charset);
        return self();
    }

    public T charset(String charset) {
        requestBuilder().charset(charset);
        return self();
    }

    public T method(Method method) {
        requestBuilder().method(method);
        return self();
    }

    public T headers(Map<String, ?> params) {
        requestBuilder().headers(params);
        return self();
    }

    public T headers(Header... headers) {
        requestBuilder().headers(headers);
        return self();
    }

    public T headers(List<Header> headers) {
        requestBuilder().headers(headers);
        return self();
    }

    public T addHeader(String key, Object value) {
        requestBuilder().addHeader(key, value);
        return self();
    }

    public T auth(String userName, String password) {
        requestBuilder().auth(userName, password);
        return self();
    }

    public T cookies(Map<String, String> cookies) {
        requestBuilder().cookies(cookies);
        return self();
    }

    public T cookies(Cookie... cookies) {
        requestBuilder().cookies(cookies);
        return self();
    }

    public T cookies(Collection<Cookie> cookies) {
        requestBuilder().cookies(cookies);
        return self();
    }

    public T addCookie(String name, String value) {
        requestBuilder().addCookie(name, value);
        return self();
    }

    public T session(Session session) {
        requestBuilder().session(session);
        return self();
    }
}
