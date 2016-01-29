package net.dongliu.requests;

/**
 * Mixed client builder and request builder for Requests utils method
 */
public class MixinHeadOnlyRequestBuilder
        extends AbstractMixinRequestBuilder<MixinHeadOnlyRequestBuilder, HeadOnlyRequestBuilder>
        implements ClientBuilderInterface<MixinHeadOnlyRequestBuilder>, BaseRequestBuilderInterface<MixinHeadOnlyRequestBuilder> {
    private final HeadOnlyRequestBuilder requestBuilder;

    MixinHeadOnlyRequestBuilder() {
        super();
        requestBuilder = new HeadOnlyRequestBuilder();
    }

    @Override
    protected MixinHeadOnlyRequestBuilder self() {
        return this;
    }

    @Override
    protected HeadOnlyRequestBuilder requestBuilder() {
        return requestBuilder;
    }
}
