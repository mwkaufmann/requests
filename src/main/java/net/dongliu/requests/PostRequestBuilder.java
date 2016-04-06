package net.dongliu.requests;

/**
 * @author Liu Dong
 */
public class PostRequestBuilder extends AbstractBodyRequestBuilder<PostRequestBuilder>
        implements IPostRequestBuilder<PostRequestBuilder> {

    protected PostRequestBuilder() {
    }

    @Override
    public Request build() {
        return new Request(method, url, parameters, headers, httpBody, charset, authInfo, cookies);
    }

    @Override
    protected PostRequestBuilder self() {
        return this;
    }
}
