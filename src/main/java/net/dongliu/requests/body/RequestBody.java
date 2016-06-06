package net.dongliu.requests.body;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * Request body parent class
 *
 * @author Liu Dong
 */
public abstract class RequestBody<T> {
    private final T body;
    private String contentType;
    /**
     * If write charset to contentType
     */
    private final boolean includeCharset;

    public RequestBody(T body, String contentType, boolean includeCharset) {
        this.body = body;
        this.contentType = contentType;
        this.includeCharset = includeCharset;
    }

    public T getBody() {
        return body;
    }

    /**
     * Set content-type value for this request body
     */
    public RequestBody<T> setContentType(String contentType) {
        this.contentType = Objects.requireNonNull(contentType);
        return this;
    }

    /**
     * the content type
     *
     * @return may be null if no content type is set
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * If write charset to contentType
     */
    public boolean isIncludeCharset() {
        return includeCharset;
    }

    /**
     * Write Request body.
     * Note: os should not be closed when this method finished.
     */
    public abstract void writeBody(OutputStream os, Charset charset) throws IOException;

    /**
     * Create request body send json data
     */
    public static <T> RequestBody<T> json(T value) {
        return new JsonRequestBody<>(value);
    }

    /**
     * Create request body send string data
     */
    public static RequestBody<String> text(String value) {
        return new StringRequestBody(Objects.requireNonNull(value));
    }

    /**
     * Create request body send x-www-form-encoded data
     */
    public static RequestBody<Collection<? extends Map.Entry<String, ?>>>
    form(Collection<? extends Map.Entry<String, ?>> value) {
        return new FormRequestBody(Objects.requireNonNull(value));
    }

    /**
     * Create request body send byte array data
     */
    public static RequestBody<byte[]> bytes(byte[] value) {
        return new BytesRequestBody(Objects.requireNonNull(value));
    }

    /**
     * Create request body from input stream
     */
    public static RequestBody<InputStream> inputStream(InputStream in) {
        return new InputStreamRequestBody(Objects.requireNonNull(in));
    }

    /**
     * Create request body from file
     */
    public static RequestBody<File> file(File file) {
        return new FileRequestBody(Objects.requireNonNull(file));
    }

    /**
     * Create multi-part post request body
     */
    public static RequestBody<Collection<? extends Part>> multiPart(Collection<? extends Part> parts) {
        return new MultiPartRequestBody(Objects.requireNonNull(parts));
    }
}
