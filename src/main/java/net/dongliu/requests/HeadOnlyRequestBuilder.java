package net.dongliu.requests;

/**
 * @author Liu Dong
 */
public class HeadOnlyRequestBuilder extends RequestBuilder<HeadOnlyRequestBuilder> {
    protected HeadOnlyRequestBuilder() {
    }

    @Override
    Request buildRequest() {
        return new Request(method, url, parameters, headers, null, charset, authInfo, cookies);
    }

    @Override
    protected HeadOnlyRequestBuilder self() {
        return this;
    }
}
