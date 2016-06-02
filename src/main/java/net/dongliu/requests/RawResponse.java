package net.dongliu.requests;

import net.dongliu.requests.json.JsonLookup;
import net.dongliu.requests.json.TypeInfer;

import java.io.*;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Raw http response
 *
 * @author Liu Dong
 */
public class RawResponse implements AutoCloseable {
    private final int statusCode;
    final Set<Cookie> cookies;
    private final List<Map.Entry<String, String>> headers;
    private final InputStream in;
    private final HttpURLConnection conn;
    // redirect history

    RawResponse(int statusCode, List<Map.Entry<String, String>> headers, Set<Cookie> cookies, InputStream in,
                HttpURLConnection conn) {
        this.statusCode = statusCode;
        this.headers = Collections.unmodifiableList(headers);
        this.cookies = Collections.unmodifiableSet(cookies);
        this.in = in;
        this.conn = conn;
    }

    @Override
    public void close() {
        IOUtils.closeQuietly(in);
        conn.disconnect();
    }


    /**
     * Read response body to string. return empty string if response has no body
     */
    public String readToText() {
        Charset charset = getCharsetFromHeaders().orElse(UTF_8);
        return readToText(charset);
    }

    /**
     * Read response body to string. return empty string if response has no body
     */
    public String readToText(Charset charset) {
        try (Reader reader = new InputStreamReader(in, charset)) {
            return IOUtils.readAll(reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } finally {
            close();
        }
    }

    private Map<String, List<Cookie>> convertCookies(Set<Cookie> cookies) {
        Map<String, List<Cookie>> map = new HashMap<>();
        for (Cookie cookie : cookies) {
            map.computeIfAbsent(cookie.getName(), name -> new LinkedList<>()).add(cookie);
        }
        return map;
    }

    private Map<String, List<String>> convertHeaders(List<Map.Entry<String, String>> headers) {
        Map<String, List<String>> map = new HashMap<>();
        for (Map.Entry<String, String> header : headers) {
            map.computeIfAbsent(header.getKey(), name -> new LinkedList<>()).add(header.getValue());
        }
        return map;
    }

    /**
     * Read response body to byte array. return empty byte array if response has no body
     */
    public byte[] readToBytes() {
        try {
            return IOUtils.readAll(in);
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
            return JsonLookup.getInstance().lookup().unmarshal(new InputStreamReader(in, charset), type);
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
            Charset charset = getCharsetFromHeaders().orElse(StandardCharsets.UTF_8);
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
                IOUtils.copy(in, fos);
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
            IOUtils.copy(in, out);
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
            IOUtils.discard(in);
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
    public InputStream getIn() {
        return in;
    }

    /**
     * Get header value with name
     */
    public Optional<String> getFirstHeader(String name) {
        return this.headers.stream().filter(h -> h.getKey().equalsIgnoreCase(name))
                .map(Map.Entry::getValue).findFirst();
    }

    /**
     * Return immutable response header list
     */
    public List<Map.Entry<String, String>> getHeaders() {
        return headers;
    }

    /**
     * Get all headers values with name
     */
    public List<String> getHeaders(String name) {
        return this.headers.stream().filter(h -> h.getKey().equalsIgnoreCase(name))
                .map(Map.Entry::getValue).collect(Collectors.toList());
    }

    /**
     * Get all cookies
     */
    public Collection<Cookie> getCookies() {
        return cookies;
    }

    private Optional<Charset> getCharsetFromHeaders() {
        Optional<String> contentType = getFirstHeader(HttpHeaders.NAME_CONTENT_TYPE);
        if (!contentType.isPresent()) {
            return Optional.empty();
        }
        String[] items = contentType.get().split("; ");
        return Arrays.stream(items).filter(it -> it.startsWith("charset="))
                .map(it -> Charset.forName(it.substring("charset=".length()).trim()))
                .findAny();
    }
}
