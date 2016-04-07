package net.dongliu.requests.struct;

import org.apache.http.annotation.Immutable;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * for multi part request
 *
 * @author Dong Liu dongliu@live.cn
 */
@Immutable
public class Part {
    private final Type type;
    // the filed name name
    private final String name;
    // the file content type
    @Nullable
    private final String mime;
    // the file name, can be null
    @Nullable
    private final String fileName;
    // the file for multi part upload
    @Nullable
    private final File file;
    @Nullable
    private final InputStream in;
    @Nullable
    private final byte[] bytes;
    @Nullable
    private final String value;

    private Part(String name, String value) {
        this.file = null;
        this.mime = null;
        this.name = name;
        this.type = Type.TEXT;
        this.in = null;
        this.bytes = null;
        this.value = value;
        this.fileName = null;
    }

    /**
     * get multiPart from file path
     */
    private Part(String name, File file) {
        this(name, URLConnection.guessContentTypeFromName(file.getName()), file);
    }

    /**
     * get multiPart from file path
     */
    private Part(String name, String mime, File file) {
        this.file = file;
        this.mime = mime;
        this.name = name;
        this.type = Type.FILE;
        this.in = null;
        this.bytes = null;
        this.value = null;
        this.fileName = file.getName();
    }

    /**
     * get multiPart from file path
     */
    private Part(String name, String mime, String fileName, InputStream in) {
        this.file = null;
        this.in = in;
        this.bytes = null;
        this.mime = mime;
        this.name = name;
        this.type = Type.STREAM;
        this.value = null;
        this.fileName = fileName;
    }

    /**
     * get multiPart from file path
     */
    private Part(String name, String mime, String fileName, byte[] bytes) {
        this.file = null;
        this.in = null;
        this.bytes = bytes;
        this.mime = mime;
        this.name = name;
        this.type = Type.BYTES;
        this.value = null;
        this.fileName = fileName;
    }

    /**
     * Create a file multi part field
     */
    public static Part filePart(String fieldName, File file) {
        try {
            return new Part(fieldName, Files.probeContentType(file.toPath()), file);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Create a file multi part field
     */
    public static Part filePart(String name, String mime, String fileName, byte[] bytes) {
        return new Part(name, mime, fileName, bytes);
    }

    /**
     * Create a file multi part field
     */
    public static Part filePart(String name, String mime, String fileName, InputStream in) {
        return new Part(name, mime, fileName, in);
    }

    /**
     * Create a text part
     */
    public static Part textPart(String name, String value) {
        return new Part(name, value);
    }

    public Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    @Nullable
    public String getMime() {
        return mime;
    }

    @Nullable
    public String getFileName() {
        return fileName;
    }

    @Nullable
    public File getFile() {
        return file;
    }

    @Nullable
    public InputStream getIn() {
        return in;
    }

    @Nullable
    public byte[] getBytes() {
        return bytes;
    }

    @Nullable
    public String getValue() {
        return value;
    }

    public enum Type {
        FILE, STREAM, BYTES, TEXT
    }
}
