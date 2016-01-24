package net.dongliu.requests;

/**
 * @author Liu Dong
 */
public class BodyRequestBuilder extends AbstractBodyRequestBuilder<BodyRequestBuilder> {
    protected BodyRequestBuilder() {
    }

    @Override
    Request buildRequest() {
        return new Request(method, url, parameters, headers, httpBody, charset, authInfo, cookies);
    }

    @Override
    protected BodyRequestBuilder self() {
        return this;
    }
}
