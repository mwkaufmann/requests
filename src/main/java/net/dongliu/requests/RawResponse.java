package net.dongliu.requests;

import net.dongliu.commons.exception.Exceptions;
import net.dongliu.commons.io.Closeables;
import net.dongliu.commons.io.InputOutputs;
import net.dongliu.commons.io.ReaderWriters;
import net.dongliu.requests.json.JsonLookup;
import net.dongliu.requests.json.TypeInfer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Raw http response.
 * It you do not consume http response body, with readToText, readToBytes, writeToFile, toTextResponse,
 * toJsonResponse, etc.., you need to close this raw response manually
 *
 * @author Liu Dong
 */
public class RawResponse implements AutoCloseable {
    private final int statusCode;
    private final String statusLine;
    private final Set<Cookie> cookies;
    private final ResponseHeaders headers;
    private final InputStream input;
    private final HttpURLConnection conn;
    @Nullable
    private Charset charset;

    RawResponse(int statusCode, String statusLine, ResponseHeaders headers, Set<Cookie> cookies, InputStream input,
                HttpURLConnection conn) {
        this.statusCode = statusCode;
        this.statusLine = statusLine;
        this.headers = headers;
        this.cookies = Collections.unmodifiableSet(cookies);
        this.input = input;
        this.conn = conn;
    }

    @Override
    public void close() {
        Closeables.closeQuietly(input);
        conn.disconnect();
    }

    /**
     * Set response read charset.
     * If not set, will get charset from response headers.
     */
    public RawResponse withCharset(Charset charset) {
        this.charset = Objects.requireNonNull(charset);
        return this;
    }


    /**
     * Read response body to string. return empty string if response has no body
     */
    public String readToText() {
        Charset charset = getCharset();
        try (Reader reader = new InputStreamReader(input, charset)) {
            return ReaderWriters.readAll(reader);
        } catch (IOException e) {
            throw Exceptions.sneakyThrow(e);
        } finally {
            close();
        }
    }

    /**
     * Convert to response, with body as text. The origin raw response will be closed
     */
    public Response<String> toTextResponse() {
        return new Response<>(this.statusCode, this.cookies, this.headers, readToText());
    }

    /**
     * Read response body to byte array. return empty byte array if response has no body
     */
    public byte[] readToBytes() {
        try {
            return InputOutputs.readAll(input);
        } catch (IOException e) {
            throw Exceptions.sneakyThrow(e);
        } finally {
            close();
        }
    }

    /**
     * Convert to response, with body as byte array
     */
    public Response<byte[]> toBytesResponse() {
        return new Response<>(this.statusCode, this.cookies, this.headers, readToBytes());
    }

    /**
     * Deserialize response content as json
     *
     * @return null if json value is null or empty
     */
    public <T> T readToJson(Type type) {
        try {
            return JsonLookup.getInstance().lookup().unmarshal(input, getCharset(), type);
        } catch (IOException e) {
            throw Exceptions.sneakyThrow(e);
        } finally {
            close();
        }
    }

    /**
     * Deserialize response content as json
     *
     * @return null if json value is null or empty
     */
    public <T> T readToJson(TypeInfer<T> typeInfer) {
        return readToJson(typeInfer.getType());
    }

    /**
     * Deserialize response content as json
     *
     * @return null if json value is null or empty
     */
    public <T> T readToJson(Class<T> cls) {
        return readToJson((Type) cls);
    }

    /**
     * Convert http response body to json result
     */
    public <T> Response<T> toJsonResponse(TypeInfer<T> typeInfer) {
        return new Response<>(this.statusCode, this.cookies, this.headers, readToJson(typeInfer));
    }

    /**
     * Convert http response body to json result
     */
    public <T> Response<T> toJsonResponse(Class<T> cls) {
        return new Response<>(this.statusCode, this.cookies, this.headers, readToJson(cls));
    }

    /**
     * Write response body to file
     */
    public void writeToFile(File path) {
        try {
            try (OutputStream os = new FileOutputStream(path)) {
                InputOutputs.copy(input, os);
            }
        } catch (IOException e) {
            throw Exceptions.sneakyThrow(e);
        } finally {
            close();
        }
    }

    /**
     * Write response body to file
     */
    public void writeToFile(Path path) {
        try {
            try (OutputStream os = Files.newOutputStream(path)) {
                InputOutputs.copy(input, os);
            }
        } catch (IOException e) {
            throw Exceptions.sneakyThrow(e);
        } finally {
            close();
        }
    }


    /**
     * Write response body to file
     */
    public void writeToFile(String path) {
        try {
            try (OutputStream os = new FileOutputStream(path)) {
                InputOutputs.copy(input, os);
            }
        } catch (IOException e) {
            throw Exceptions.sneakyThrow(e);
        } finally {
            close();
        }
    }

    /**
     * Write response body to output stream. Output stream will not be closed.
     */
    public void writeTo(OutputStream out) {
        try {
            InputOutputs.copy(input, out);
        } catch (IOException e) {
            throw Exceptions.sneakyThrow(e);
        } finally {
            close();
        }
    }

    /**
     * Consume and discard this response body
     */
    public void discardBody() {
        try {
            InputOutputs.skipAll(input);
        } catch (IOException e) {
            throw Exceptions.sneakyThrow(e);
        } finally {
            close();
        }
    }

    /**
     * The response status code
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Get the status line
     */
    public String getStatusLine() {
        return statusLine;
    }

    /**
     * The response body input stream
     */
    public InputStream getInput() {
        return input;
    }

    /**
     * Get header value with name. If not exists, return null
     */
    @Nullable
    public String getFirstHeader(String name) {
        return this.headers.getFirstHeader(name);
    }

    /**
     * Return immutable response header list
     */
    @Nonnull
    public List<Map.Entry<String, String>> getHeaders() {
        return headers.getHeaders();
    }

    /**
     * Get all headers values with name. If not exists, return empty list
     */
    @Nonnull
    public List<String> getHeaders(String name) {
        return this.headers.getHeaders(name);
    }

    /**
     * Get all cookies
     */
    public Collection<Cookie> getCookies() {
        return cookies;
    }

    private Charset getCharset() {
        if (this.charset != null) {
            return this.charset;
        }
        String contentType = getFirstHeader(HttpHeaders.NAME_CONTENT_TYPE);
        if (contentType == null) {
            return StandardCharsets.UTF_8;
        }
        String[] items = contentType.split("; ");
        for (String item : items) {
            int idx = item.indexOf('=');
            if (idx < 0) {
                continue;
            }
            String key = item.substring(0, idx).trim();
            if (key.equalsIgnoreCase("charset")) {
                return Charset.forName(item.substring(idx + 1).trim());
            }
        }
        return StandardCharsets.UTF_8;
    }
}
