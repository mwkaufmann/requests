package net.dongliu.requests;

import net.dongliu.requests.exception.RequestException;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;

public class RawResponse implements Closeable {
    private final CloseableHttpResponse closeableHttpResponse;
    private final PooledClient client;
    private final boolean closeOnFinished;

    private final int status;
    private final List<Header> headers;
    private final List<Cookie> cookies;
    private final List<URI> history;

    RawResponse(CloseableHttpResponse closeableHttpResponse, PooledClient client,
                boolean closeOnFinished, int status, List<Header> headers,
                List<Cookie> cookies, List<URI> history) {
        this.closeableHttpResponse = closeableHttpResponse;
        this.client = client;
        this.closeOnFinished = closeOnFinished;
        this.status = status;
        this.headers = Collections.unmodifiableList(headers);
        this.cookies = Collections.unmodifiableList(cookies);
        this.history = history == null ? Collections.emptyList() : Collections.unmodifiableList(history);
    }

    /**
     * Use custom handler to handle http response
     */
    public <R> R process(ResponseProcessor<R> processor) throws RequestException {
        try {
            HttpEntity entity = closeableHttpResponse.getEntity();
            if (entity == null) {
                entity = new ByteArrayEntity(new byte[0]);
            }
            return processor.convert(status, headers, entity);
        } catch (IOException e) {
            throw new RequestException(e);
        } finally {
            close();
        }
    }

    /**
     * Get http response for return text result.
     * Decode response body to text with charset provided
     */
    public String readToText(Charset charset) throws RequestException {
        return process((code, headers, entity) -> EntityUtils.toString(entity, charset));
    }

    /**
     * Get http response for return text result.
     * Decode response body to text with charset get from response header
     */
    public String readToText() throws RequestException {
        return process((code, headers, entity) -> EntityUtils.toString(entity));
    }

    /**
     * get http response for return byte array result.
     */
    public byte[] readToBytes() throws RequestException {
        return process((code, headers, entity) -> EntityUtils.toByteArray(entity));
    }

    /**
     * Write output stream to file if response status is 20x
     *
     * @return false if response code is not 20x
     * @throws UncheckedIOException if get io error
     */
    public boolean writeTo(File file) throws RequestException {
        return process((code, headers, entity) -> {
            if (code < 200 || code >= 300) {
                return false;
            } else {
                try (OutputStream out = new FileOutputStream(file)) {
                    IOUtils.copy(entity.getContent(), out);
                }
                return true;
            }
        });
    }

    /**
     * Write response content to output stream
     *
     * @return false if response code is not 20x
     * @throws UncheckedIOException if get io error
     */
    public boolean writeTo(OutputStream out) throws RequestException {
        return process((code, headers, entity) -> {
            if (code < 200 || code >= 300) {
                return false;
            } else {
                IOUtils.copy(entity.getContent(), out);
                return true;
            }
        });
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
