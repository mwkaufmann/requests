package net.dongliu.requests;

import net.dongliu.requests.body.RequestBody;
import net.dongliu.requests.exception.RequestsException;

import javax.annotation.Nonnull;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.*;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Stream;
import java.util.zip.DeflaterInputStream;
import java.util.zip.GZIPInputStream;

import static java.util.stream.Collectors.*;
import static net.dongliu.requests.HttpHeaders.*;

/**
 * Http request
 *
 * @author Liu Dong
 */
public class HttpRequest {
    static final int DEFAULT_TIMEOUT = 3000;

    private final String method;
    private final String url;
    private final Collection<Map.Entry<String, String>> headers;
    private final Collection<Map.Entry<String, String>> cookies;
    private final String userAgent;
    private final Collection<Map.Entry<String, String>> params;
    private final Charset requestCharset;
    private final RequestBody<?> body;
    private final int readTimeout;
    private final int connectTimeout;
    private final Proxy proxy;
    private final boolean followRedirect;
    private final boolean compress;
    private final boolean verify;
    private final List<CertificateInfo> certs;
    private final BasicAuth basicAuth;
    @Nonnull
    private final Session session;
    private final URL fullUrl;

    HttpRequest(RequestBuilder builder) {
        method = builder.method;
        url = builder.url;
        headers = builder.headers;
        cookies = builder.cookies;
        userAgent = builder.userAgent;
        params = builder.params;
        requestCharset = builder.requestCharset;
        body = builder.body;
        readTimeout = builder.socksTimeout;
        connectTimeout = builder.connectTimeout;
        proxy = builder.proxy;
        followRedirect = builder.followRedirect;
        compress = builder.compress;
        verify = builder.verify;
        certs = builder.certs;
        basicAuth = builder.basicAuth;
        session = builder.session;

        this.fullUrl = Utils.joinUrl(url, params, requestCharset);
    }

    // handle redirect here
    RawResponse handleRequest() {
        RawResponse response = doRequest();

        if (!followRedirect || !Utils.isRedirect(response.getStatusCode())) {
            return response;
        }

        // handle redirect
        response.discardBody();
        int redirectCount = 0;
        URL redirectUrl = fullUrl;
        while (redirectCount++ < 5) {
            Optional<String> location = response.getFirstHeader(NAME_LOCATION);
            if (!location.isPresent()) {
                throw new RequestsException("Redirect location not found");
            }
            try {
                redirectUrl = new URL(redirectUrl, location.get());
            } catch (MalformedURLException e) {
                throw new RequestsException("Get redirect url error", e);
            }
            response = session.get(redirectUrl.toExternalForm())
                    .proxy(proxy)
                    .followRedirect(false).send();
            if (!Utils.isRedirect(response.getStatusCode())) {
                return response;
            }
            response.discardBody();
        }
        throw new RequestsException("Too many redirect");
    }


    private RawResponse doRequest() {
        Charset charset = requestCharset;
        String protocol = fullUrl.getProtocol();
        String host = fullUrl.getHost(); // only domain, do not have port
        String path = fullUrl.getPath();
        String cookiePath;
        int idx = path.lastIndexOf('/');
        if (idx >= 0) {
            cookiePath = path.substring(0, idx + 1);
        } else {
            cookiePath = "/";
        }

        HttpURLConnection conn;
        try {
            if (proxy != null) {
                conn = (HttpURLConnection) fullUrl.openConnection(proxy);
            } else {
                conn = (HttpURLConnection) fullUrl.openConnection();
            }
        } catch (MalformedURLException e) {
            throw new RequestsException(e);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        // deal with https
        if (conn instanceof HttpsURLConnection) {
            // do not check cert
            TrustManager trustManager = null;
            if (!verify) {
                trustManager = new TrustAllTrustManager();
            } else if (!certs.isEmpty()) {
                trustManager = new CustomCertTrustManager(certs);
            }
            if (trustManager != null) {
                SSLContext sslContext;
                try {
                    sslContext = SSLContext.getInstance("SSL");
                    sslContext.init(null, new TrustManager[]{trustManager}, new SecureRandom());
                } catch (NoSuchAlgorithmException | KeyManagementException e) {
                    throw new RequestsException(e);
                }

                SSLSocketFactory ssf = sslContext.getSocketFactory();
                ((HttpsURLConnection) conn).setSSLSocketFactory(ssf);
            }
        }

        try {
            conn.setRequestMethod(method);
        } catch (ProtocolException e) {
            throw new RequestsException(e);
        }
        conn.setReadTimeout(readTimeout);
        conn.setConnectTimeout(connectTimeout);
        // Url connection did not deal with cookie when handle redirect. Disable it and handle it manually
        conn.setInstanceFollowRedirects(false);
        if (body != null) {
            conn.setDoOutput(true);
        }

        // headers
        if (!userAgent.isEmpty()) {
            conn.setRequestProperty(NAME_USER_AGENT, userAgent);
        }
        if (compress) {
            conn.setRequestProperty(NAME_ACCEPT_ENCODING, "gzip, deflate");
        }

        if (basicAuth != null) {
            conn.setRequestProperty(NAME_AUTHORIZATION, basicAuth.encode());
        }

        // this should exists before set custom request contentType or headers,to make sure user can overwrite this
        if (body != null) {
            String contentType = body.getContentType();
            if (contentType != null) {
                if (body.isIncludeCharset()) {
                    contentType += "; charset=" + requestCharset.name().toLowerCase();
                }
                conn.setRequestProperty(NAME_CONTENT_TYPE, contentType);
            }
        }

        // set cookies
        if (!cookies.isEmpty() || !session.getCookies().isEmpty()) {
            Stream<? extends Map.Entry<String, String>> stream1 = cookies.stream();
            Stream<? extends Map.Entry<String, String>> stream2 = session.matchedCookies(protocol, host, cookiePath);
            String cookieStr = Stream.concat(stream1, stream2)
                    .map(c -> c.getKey() + "=" + c.getValue())
                    .collect(joining("; "));
            if (!cookieStr.isEmpty()) {
                conn.setRequestProperty(NAME_COOKIE, cookieStr);
            }
        }

        for (Map.Entry<String, String> header : headers) {
            conn.setRequestProperty(header.getKey(), header.getValue());
        }

        try {
            conn.connect();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        try {
            // send body
            if (body != null) {
                sendBody(body, conn, charset);
            }
            return getResponse(conn, host, path);
        } catch (IOException e) {
            conn.disconnect();
            throw new UncheckedIOException(e);
        } catch (Throwable e) {
            conn.disconnect();
            throw e;
        }
    }

    /**
     * Wrap response, deal with headers and cookies
     *
     * @throws IOException
     */
    private RawResponse getResponse(HttpURLConnection conn, String host, String path) throws IOException {
        // read result
        int status = conn.getResponseCode();

        // headers and cookies
        List<Map.Entry<String, String>> headers = new ArrayList<>();
        Set<Cookie> cookies = new HashSet<>();
        int index = 0;
        while (true) {
            String key = conn.getHeaderFieldKey(index);
            String value = conn.getHeaderField(index);
            if (value == null) {
                break;
            }
            index++;
            //status line
            if (key == null) {
                continue;
            }
            headers.add(Parameter.of(key, value));
            if (key.equalsIgnoreCase(NAME_SET_COOKIE)) {
                cookies.add(CookieUtils.parseCookieHeader(host, path, value));
            }
        }

        // deal with [compressed] input
        InputStream input;
        if (status >= 200 && status < 400) {
            input = conn.getInputStream();
        } else {
            input = conn.getErrorStream();
        }
        input = wrapCompressBody(status, headers, input);

        // update session
        session.updateCookie(cookies);
        return new RawResponse(status, headers, cookies, input, conn);
    }

    /**
     * Wrap response input stream if it is compressed, return input its self if not use compress
     */
    private InputStream wrapCompressBody(int status, List<Map.Entry<String, String>> headers, InputStream input) {

        // if has no boddy
        if (method.equals("HEAD")) {
            return input;
        }
        if ((status >= 100 && status < 200) || status == 304 || status == 204) {
            return input;
        }

        Optional<String> encoding = headers.stream()
                .filter(h -> h.getKey().equalsIgnoreCase(NAME_CONTENT_ENCODING))
                .map(Map.Entry::getValue).findAny();
        if (!encoding.isPresent()) {
            return input;
        }
        String contentEncoding = encoding.get();
        switch (contentEncoding) {
            case "gzip":
                try {
                    return new GZIPInputStream(input);
                } catch (IOException e) {
                    IOUtils.closeQuietly(input);
                    throw new UncheckedIOException(e);
                }
            case "deflate":
                return new DeflaterInputStream(input);
            //            "identity" -> input
            //            "compress" -> input //historic; deprecated in most applications and replaced by gzip or deflate
            default:
                return input;
        }

    }


    private void sendBody(RequestBody body, HttpURLConnection conn, Charset requestCharset) {
        try (OutputStream os = conn.getOutputStream()) {
            body.writeBody(os, requestCharset);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    static RequestBuilder newBuilder(Session session) {
        return new RequestBuilder(session);
    }
}
