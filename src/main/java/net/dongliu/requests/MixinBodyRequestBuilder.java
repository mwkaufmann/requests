package net.dongliu.requests;

import net.dongliu.requests.struct.HttpBody;

import java.io.InputStream;

/**
 * Mixed client builder and request builder for Requests utils method
 */
public class MixinBodyRequestBuilder extends AbstractMixinRequestBuilder<MixinBodyRequestBuilder, BodyRequestBuilder>
        implements IClientBuilder<MixinBodyRequestBuilder>, IBodyRequestBuilder<MixinBodyRequestBuilder> {
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

    public MixinBodyRequestBuilder body(byte[] data) {
        bodyRequestBuilder.body(data);
        return this;
    }

    public MixinBodyRequestBuilder body(InputStream in) {
        bodyRequestBuilder.body(in);
        return this;
    }

    public MixinBodyRequestBuilder body(String body) {
        bodyRequestBuilder.body(body);
        return this;
    }

    @Override
    public MixinBodyRequestBuilder body(HttpBody body) {
        bodyRequestBuilder.body(body);
        return this;
    }

}
