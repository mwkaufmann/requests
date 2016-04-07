package net.dongliu.requests;

import net.dongliu.requests.exception.IllegalStatusException;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;

import javax.annotation.Nullable;
import java.io.*;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

/**
 * Http response.
 * Http response must be consumed using close/readToText/readToBytes/writeTo or other similar methods,
 * resource may leak if not.
 */
public class RawResponse implements Closeable {
    private final CloseableHttpResponse closeableHttpResponse;
    private final PooledClient client;
    private final boolean closeOnFinished;

    private final int status;
    private final List<Header> headers;
    private final List<Cookie> cookies;
    private final List<URI> history;

    @Nullable
    private Charset charset;

    RawResponse(CloseableHttpResponse closeableHttpResponse, PooledClient client,
                boolean closeOnFinished, int status, List<Header> headers,
                List<Cookie> cookies, @Nullable List<URI> history) {
        this.closeableHttpResponse = closeableHttpResponse;
        this.client = client;
        this.closeOnFinished = closeOnFinished;
        this.status = status;
        this.headers = Collections.unmodifiableList(headers);
        this.cookies = Collections.unmodifiableList(cookies);
        this.history = history == null ? Collections.emptyList() : Collections.unmodifiableList(history);
    }

    /**
     * If status is not valid(2xx or 3xx), throw IllegalStatusException
     */
    public RawResponse checkStatusValid() throws IllegalStatusException {
        if (status < 200 || status >= 400) {
            close();
            throw new IllegalStatusException(status);
        }
        return this;
    }

    /**
     * If status is not 2xx, throw IllegalStatusException
     */
    public RawResponse checkStatus2xx() throws IllegalStatusException {
        if (status < 200 || status >= 300) {
            close();
            throw new IllegalStatusException(status);
        }
        return this;
    }

    /**
     * Force use charset to wrap input stream to reader for text-based handler use
     * If not set, use charset got from http resp header; if not exists, use UTF-8
     */
    public RawResponse responseCharset(Charset charset) {
        this.charset = charset;
        return this;
    }

    /**
     * Use custom handler to handle http response.
     *
     * @return null if entity not exists
     */
    @Nullable
    public <R> R process(ResponseProcessor<R> processor) throws UncheckedIOException {
        try {
            HttpEntity entity = closeableHttpResponse.getEntity();
            if (entity == null) {
                return null;
            }
            ContentType contentType = ContentType.get(entity);
            long contentLength = entity.getContentLength();

            try (InputStream in = entity.getContent()) {
                if (in == null) {
                    return null;
                }
                ResponseProcessor.ResponseData responseData = new ResponseProcessor.ResponseData(
                        status, headers, contentLength, contentType, in);
                return processor.convert(responseData);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } finally {
            close();
        }
    }

    /**
     * Unmarshal http response to instance of type T.
     *
     * @return null if http body not exists
     * @throws IllegalStatusException if status is 2xx
     */
    @Nullable
    public <T> T unmarshalBin(Function<InputStream, T> function) {
        checkStatus2xx();
        return process(resp -> function.apply(resp.getIn()));
    }

    /**
     * Unmarshal http response to instance of type T.
     *
     * @return null if http body not exists
     * @throws IllegalStatusException if status is 2xx
     */
    @Nullable
    public <T> T unmarshalText(Function<Reader, T> function) {
        checkStatus2xx();
        return process(resp -> {
            try (Reader reader = new InputStreamReader(resp.getIn(), getCharset())) {
                return function.apply(reader);
            }
        });
    }

    /**
     * Get http response for return text result.
     * Decode response body to text with charset get from response header, use UTF-8 if not found
     */
    public String readToText() throws UncheckedIOException {
        String str = process(respData ->
                IOUtils.toString(new InputStreamReader(respData.getIn(), getCharset()),
                        Math.toIntExact(byteLen2CharLen(respData.getContentLen()))));
        return str == null ? "" : str;
    }

    private long byteLen2CharLen(long byteLen) {
        if (byteLen < 0) {
            return byteLen;
        }

        if (byteLen < 1024 * 8) {
            // now just return byteLen..
            return byteLen;
        }
        return (long) (byteLen / 1.5);
    }

    /**
     * get http response for return byte array result.
     */
    public byte[] readToBytes() throws UncheckedIOException {
        byte[] bytes = process(respData -> IOUtils.toBytes(respData.getIn(), Math.toIntExact(respData.getContentLen())));
        return bytes == null ? new byte[0] : IOUtils.emptyByteArray;
    }

    /**
     * Write output stream to file
     *
     * @return false if http response body not exists
     * @throws UncheckedIOException if get io error
     */
    public boolean writeTo(File file) throws UncheckedIOException {
        Boolean result = process(responseData -> {
            try (OutputStream out = new FileOutputStream(file)) {
                IOUtils.copy(responseData.getIn(), out, responseData.getContentLen());
            }
            return true;
        });
        if (result == null) {
            // no http response entity
            return false;
        }
        return result;
    }

    /**
     * Write response content to output stream
     *
     * @return false if response code is not 20x
     * @throws UncheckedIOException if get io error
     */
    public boolean writeTo(OutputStream out) throws UncheckedIOException {
        Boolean result = process(responseData -> {
            IOUtils.copy(responseData.getIn(), out, responseData.getContentLen());
            return true;
        });
        if (result == null) {
            // no http response entity
            return false;
        }
        return result;
    }

    /**
     * Write response content to writer, use charset from http header, use UTF-8 if not found
     */
    public boolean writeTo(Writer writer) {
        Boolean result = process(respData -> {
            IOUtils.copy(new InputStreamReader(respData.getIn(), getCharset()), writer,
                    byteLen2CharLen(respData.getContentLen()));
            return true;
        });
        if (result == null) {
            // no http response entity
            return false;
        }
        return result;
    }

    private Charset getCharset() {
        if (this.charset != null) {
            return this.charset;
        }

        ContentType contentType = ContentType.get(closeableHttpResponse.getEntity());
        if (contentType != null && contentType.getCharset() != null) {
            return contentType.getCharset();
        }
        return StandardCharsets.UTF_8;
    }

    /**
     * Http response status code
     */
    public int getStatus() {
        return status;
    }

    /**
     * Response header.
     *
     * @return immutable non-null list
     */
    public List<Header> getHeaders() {
        return headers;
    }

    /**
     * All cookies of current session
     *
     * @return immutable non-null list
     */
    public List<Cookie> getCookies() {
        return cookies;
    }


    /**
     * Get first match header value
     */
    @Nullable
    public String getFirstHeader(String name) {
        return headers.stream().filter(h -> h.getName().equals(name)).findFirst().map(Header::getValue).orElse(null);
    }

    /**
     * Get headers value by name
     */
    public List<String> getHeaders(String name) {
        return headers.stream().filter(h -> h.getName().equals(name)).map(Header::getValue).collect(toList());
    }


    /**
     * Get first match cookie
     */
    @Nullable
    public Cookie getFirstCookie(String name) {
        return cookies.stream().filter(h -> h.getName().equals(name)).findFirst().orElse(null);
    }

    /**
     * Get cookies by name
     */
    public List<Cookie> getCookies(String name) {
        return cookies.stream().filter(h -> h.getName().equals(name)).collect(toList());
    }

    /**
     * Redirect history url.
     *
     * @return immutable non-null list. empty list if no history
     */
    public List<URI> getHistory() {
        return history;
    }

    @Override
    public void close() {
        IOUtils.closeQuietly(closeableHttpResponse);
        if (closeOnFinished) {
            client.close();
        }
    }
}
