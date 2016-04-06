package net.dongliu.requests;

import net.dongliu.requests.struct.HttpBody;

/**
 * Mixed client builder and request builder for Requests utils method
 */
public class MixinPostRequestBuilder extends AbstractMixinRequestBuilder<MixinPostRequestBuilder, PostRequestBuilder>
        implements IClientBuilder<MixinPostRequestBuilder>, IPostRequestBuilder<MixinPostRequestBuilder> {
    private final PostRequestBuilder postRequestBuilder;

    public MixinPostRequestBuilder() {
        super();
        this.postRequestBuilder = new PostRequestBuilder();
    }

    @Override
    protected MixinPostRequestBuilder self() {
        return this;
    }

    @Override
    protected PostRequestBuilder requestBuilder() {
        return postRequestBuilder;
    }

    @Override
    public MixinPostRequestBuilder body(HttpBody body) {
        postRequestBuilder.body(body);
        return this;
    }
}
