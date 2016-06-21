package net.dongliu.requests;

import net.dongliu.commons.io.Closables;
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
import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Raw http response
 *
 * @author Liu Dong
 */
public class RawResponse implements AutoCloseable {
    private final int statusCode;
    private final Set<Cookie> cookies;
    private final ResponseHeaders headers;
    private final InputStream input;
    private final HttpURLConnection conn;
    // redirect history

    RawResponse(int statusCode, ResponseHeaders headers, Set<Cookie> cookies, InputStream input,
                HttpURLConnection conn) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.cookies = Collections.unmodifiableSet(cookies);
        this.input = input;
        this.conn = conn;
    }

    @Override
    public void close() {
        Closables.closeQuietly(input);
        conn.disconnect();
    }


    /**
     * Read response body to string. return empty string if response has no body
     */
    public String readToText() {
        Charset charset = getCharsetFromHeaders(UTF_8);
        return readToText(charset);
    }

    /**
     * Read response body to string. return empty string if response has no body
     */
    public String readToText(Charset charset) {
        try (Reader reader = new InputStreamReader(input, charset)) {
            return ReaderWriters.readAll(reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } finally {
            close();
        }
    }

    /**
     * Read response body to byte array. return empty byte array if response has no body
     */
    public byte[] readToBytes() {
        try {
            return InputOutputs.readAll(input);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } finally {
            close();
        }
    }

    /**
     * Deserialize response content as json
     *
     * @return null if json value is null or empty
     */
    public <T> T readAsJson(Type type, Charset charset) {
        try {
            return JsonLookup.getInstance().lookup().unmarshal(new InputStreamReader(input, charset), type);
        } finally {
            close();
        }
    }

    /**
     * Deserialize response content as json
     *
     * @return null if json value is null or empty
     */
    public <T> T readAsJson(Type type) {
        try {
            Charset charset = getCharsetFromHeaders(StandardCharsets.UTF_8);
            return readAsJson(type, charset);
        } finally {
            close();
        }
    }

    /**
     * Deserialize response content as json
     *
     * @return null if json value is null or empty
     */
    public <T> T readAsJson(TypeInfer<T> typeInfer, Charset charset) {
        return readAsJson(typeInfer.getType(), charset);
    }

    /**
     * Deserialize response content as json
     *
     * @return null if json value is null or empty
     */
    public <T> T readAsJson(TypeInfer<T> typeInfer) {
        return readAsJson(typeInfer.getType());
    }

    /**
     * Deserialize response content as json
     *
     * @return null if json value is null or empty
     */
    public <T> T readAsJson(Class<T> cls, Charset charset) {
        return readAsJson((Type) cls, charset);
    }

    /**
     * Deserialize response content as json
     *
     * @return null if json value is null or empty
     */
    public <T> T readAsJson(Class<T> cls) {
        return readAsJson((Type) cls);
    }

    /**
     * Write response body to file
     */
    public void writeToFile(File path) {
        try {
            try (FileOutputStream fos = new FileOutputStream(path)) {
                InputOutputs.copy(input, fos);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
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
            throw new UncheckedIOException(e);
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
            throw new UncheckedIOException(e);
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

    private Charset getCharsetFromHeaders(Charset defaultCharset) {
        String contentType = getFirstHeader(HttpHeaders.NAME_CONTENT_TYPE);
        if (contentType == null) {
            return defaultCharset;
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
        return defaultCharset;
    }
}
