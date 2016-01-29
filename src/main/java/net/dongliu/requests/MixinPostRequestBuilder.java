package net.dongliu.requests;

import net.dongliu.requests.struct.Parameter;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

/**
 * Mixed client builder and request builder for Requests utils method
 */
public class MixinPostRequestBuilder extends AbstractMixinRequestBuilder<MixinPostRequestBuilder, PostRequestBuilder>
        implements ClientBuilderInterface<MixinPostRequestBuilder>, PostRequestBuilderInterface<MixinPostRequestBuilder> {
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

    public MixinPostRequestBuilder addMultiPart(String name, String mimeType, File file) {
        postRequestBuilder.addMultiPart(name, mimeType, file);
        return this;
    }

    public MixinPostRequestBuilder addMultiPart(String name, String value) {
        postRequestBuilder.addMultiPart(name, value);
        return this;
    }

    public MixinPostRequestBuilder forms(Parameter... parameters) {
        postRequestBuilder.forms(parameters);
        return this;
    }

    public MixinPostRequestBuilder addMultiPart(String name, String mimeType, byte[] bytes) {
        postRequestBuilder.addMultiPart(name, mimeType, bytes);
        return this;
    }

    public MixinPostRequestBuilder addMultiPart(String name, String mimeType, String fileName, InputStream in) {
        postRequestBuilder.addMultiPart(name, mimeType, fileName, in);
        return this;
    }

    public MixinPostRequestBuilder addMultiPart(String name, String mimeType, String fileName, byte[] bytes) {
        postRequestBuilder.addMultiPart(name, mimeType, fileName, bytes);
        return this;
    }

    public MixinPostRequestBuilder forms(Map<String, ?> map) {
        postRequestBuilder.forms(map);
        return this;
    }

    public MixinPostRequestBuilder addForm(String key, Object value) {
        postRequestBuilder.addForm(key, value);
        return this;
    }

    public MixinPostRequestBuilder addMultiPart(File file) {
        postRequestBuilder.addMultiPart(file);
        return this;
    }

    public MixinPostRequestBuilder data(byte[] data) {
        postRequestBuilder.data(data);
        return this;
    }

    public MixinPostRequestBuilder data(InputStream in) {
        postRequestBuilder.data(in);
        return this;
    }

    public MixinPostRequestBuilder data(String body) {
        postRequestBuilder.data(body);
        return this;
    }

    public MixinPostRequestBuilder forms(Collection<Parameter> parameters) {
        postRequestBuilder.forms(parameters);
        return this;
    }

    public MixinPostRequestBuilder addMultiPart(String name, String mimeType, InputStream in) {
        postRequestBuilder.addMultiPart(name, mimeType, in);
        return this;
    }

    public MixinPostRequestBuilder addMultiPart(String name, File file) {
        postRequestBuilder.addMultiPart(name, file);
        return this;
    }
}
