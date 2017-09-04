package net.dongliu.requests.body;

import javax.annotation.Nullable;
import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Objects;

/**
 * One part of multipart request
 *
 * @author Liu Dong
 */
public class Part<T> implements Serializable {
    private static final long serialVersionUID = -8628605676399143391L;
    private final String name;
    /**
     * May be null
     */
    @Nullable
    private final String fileName;
    private final RequestBody<T> requestBody;

    /**
     * @param name        cannot be null
     * @param fileName    may be null if not exists
     * @param requestBody cannot be null
     */
    public Part(String name, @Nullable String fileName, RequestBody<T> requestBody) {
        this.name = Objects.requireNonNull(name);
        this.fileName = fileName;
        Objects.requireNonNull(requestBody);
        // Could not use MultiPartRequest self as a part
        if (requestBody instanceof MultiPartRequestBody) {
            throw new IllegalArgumentException("Could not use MultiPartRequest self as a part");
        }
        this.requestBody = requestBody;
    }

    /**
     * Set content type for this part. Default content-type will be set automatically
     */
    public Part<T> contentType(String contentType) {
        this.requestBody.setContentType(contentType);
        return this;
    }

    /**
     * Create a file multi-part field
     */
    public static Part<File> file(String name, File file) {
        return new Part<>(name, file.getName(), RequestBody.file(file));
    }

    /**
     * Create a file multi-part field
     */
    public static Part<InputStream> file(String name, String fileName, InputStream in) {
        return new Part<>(name, fileName, RequestBody.inputStream(in));
    }

    /**
     * Create a file multi-part field
     */
    public static Part<byte[]> file(String name, String fileName, byte[] bytes) {
        return new Part<>(name, fileName, RequestBody.bytes(bytes));
    }

    /**
     * Create a text multi-part field
     */
    public static Part<String> text(String name, String value) {
        return new Part<>(name, null, RequestBody.text(value));
    }

    /**
     * Create a (name, value) param multi-part field
     */
    public static Part<String> param(String name, String value) {
        RequestBody<String> body = RequestBody.text(value);
        body.setContentType("");
        return new Part<>(name, null, body);
    }

    public String getName() {
        return name;
    }

    /**
     * may be null if not exists
     */
    @Nullable
    public String getFileName() {
        return fileName;
    }

    public RequestBody<T> getRequestBody() {
        return requestBody;
    }

}
