package net.dongliu.requests;

import java.io.InputStream;

/**
 * Mixed client builder and request builder for Requests utils method
 */
public class MixinBodyRequestBuilder extends AbstractMixinRequestBuilder<MixinBodyRequestBuilder, BodyRequestBuilder>
        implements ClientBuilderInterface<MixinBodyRequestBuilder>, BodyRequestBuilderInterface<MixinBodyRequestBuilder> {
    private final BodyRequestBuilder bodyRequestBuilder;

    public MixinBodyRequestBuilder() {
        super();
        this.bodyRequestBuilder = new BodyRequestBuilder();
    }

    @Override
    protected MixinBodyRequestBuilder self() {
        return this;
    }

    @Override
    protected BodyRequestBuilder requestBuilder() {
        return bodyRequestBuilder;
    }

    public MixinBodyRequestBuilder data(byte[] data) {
        bodyRequestBuilder.data(data);
        return this;
    }

    public MixinBodyRequestBuilder data(InputStream in) {
        bodyRequestBuilder.data(in);
        return this;
    }

    public MixinBodyRequestBuilder data(String body) {
        bodyRequestBuilder.data(body);
        return this;
    }
}
