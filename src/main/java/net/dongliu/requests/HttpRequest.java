package net.dongliu.requests;

import net.dongliu.commons.Strings;
import net.dongliu.commons.collection.Pair;
import net.dongliu.commons.collection.Sets;
import net.dongliu.commons.exception.Exceptions;
import net.dongliu.commons.io.Closables;
import net.dongliu.requests.body.RequestBody;
import net.dongliu.requests.exception.RequestsException;

import javax.annotation.Nullable;
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
import java.time.Instant;
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
    private final Collection<Pair<String, String>> headers;
    private final Collection<Pair<String, String>> cookies;
    private final String userAgent;
    private final Collection<Pair<String, String>> params;
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

        try {
            if (session != null) {
                session.updateCookie(response.cookieList);
            }
        } catch (RuntimeException ignore) {
        } catch (Throwable e) {
            response.close();
            throw e;
        }

        if (!followRedirect || !Utils.isRedirect(response.getStatusCode())) {
            return response;
        }

        // handle redirect
        response.discardBody();
        int redirectCount = 0;
        URL redirectUrl = fullUrl;
        Session redirectSession = session == null ? Session.create(response.cookieList) : session;
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
            response = Requests.get(redirectUrl.toExternalForm()).session(redirectSession)
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
        String host = fullUrl.getHost();
        String effectivePath = Strings.beforeLast(fullUrl.getPath(), "/") + "/";

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
                    throw Exceptions.sneakyThrow(e);
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
        if (!cookies.isEmpty() || session != null) {
            Stream<String> stream1 = cookies.stream().map(e -> e.getName() + "=" + e.getValue());
            Instant now = Instant.now();
            Stream<String> stream2;
            if (session != null) {
                stream2 = session.getCookies().stream()
                        .filter(c -> !c.expired(now) && c.match(host, effectivePath))
                        .map(c -> c.getName() + "=" + c.getValue());
            } else {
                stream2 = Stream.empty();
            }

            String cookieStr = Stream.concat(stream1, stream2).collect(joining("; "));
            conn.setRequestProperty(NAME_COOKIE, cookieStr);
        }

        for (Pair<String, String> header : headers) {
            conn.setRequestProperty(header.getName(), header.getValue());
            Map<String, List<String>> requestProperties = conn.getRequestProperties();
            System.out.println(requestProperties);
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

            // read result
            int status = conn.getResponseCode();

            // headers
            List<Pair<String, String>> responseHeaders = conn.getHeaderFields().entrySet().stream()
                    // ignore status line
                    .filter(it -> it.getKey() != null)
                    .flatMap(e -> e.getValue().stream().map(v -> Pair.of(e.getKey(), v)))
                    .collect(toList());
            // cookies
            List<SetCookie> setCookieList = conn.getHeaderFields().entrySet().stream()
                    .filter(e -> NAME_SET_COOKIE.equals(e.getKey()))
                    .flatMap(e -> e.getValue().stream().map(SetCookie::parse))
                    .collect(toList());
            Set<Cookie> cookieSet = Sets.create();
            for (SetCookie setCookie : setCookieList) {
                // we do not check top domain here..
                if (setCookie.getDomain() == null || !host.endsWith(setCookie.getDomain())) {
                    setCookie.setDomain(host);
                    setCookie.setBareDomain(true);
                }
                if (setCookie.getPath() == null) {
                    setCookie.setPath(effectivePath);
                }
                List<Pair<String, String>> respCookies = setCookie.getCookies();
                for (Pair<String, String> pair : respCookies) {
                    cookieSet.add(new Cookie(setCookie.getDomain(), setCookie.isBareDomain(),
                            setCookie.getPath(), pair.getKey(),
                            pair.getValue(), setCookie.getExpiry()));
                }
            }
            InputStream input;
            if (status >= 200 && status < 400) {
                input = conn.getInputStream();
            } else {
                input = conn.getErrorStream();
            }
            InputStream wrapIn = wrap(status, responseHeaders, input);
            return new RawResponse(status, responseHeaders, cookieSet, wrapIn, conn);
        } catch (IOException e) {
            conn.disconnect();
            throw new UncheckedIOException(e);
        } catch (Throwable e) {
            conn.disconnect();
            throw e;
        }
    }

    private InputStream wrap(int status, List<Pair<String, String>> headers, InputStream input) {

        // if has no boddy
        if (method.equals("HEAD")) {
            return input;
        }
        if ((status >= 100 && status < 200) || status == 304 || status == 204) {
            return input;
        }

        String contentEncoding = getHeaderValue(headers, NAME_CONTENT_ENCODING);
        if (contentEncoding == null) {
            return input;
        }
        switch (contentEncoding) {
            case "gzip":
                try {
                    return new GZIPInputStream(input);
                } catch (IOException e) {
                    Closables.closeQuietly(input);
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

    private
    @Nullable
    String getHeaderValue(List<Pair<String, String>> headers, String name) {
        return headers.stream().filter(h -> h.getName().equalsIgnoreCase(name))
                .map(Pair::getValue).findAny().orElse(null);
    }


    private void sendBody(RequestBody body, HttpURLConnection conn, Charset requestCharset) {
        try (OutputStream os = conn.getOutputStream()) {
            body.writeBody(os, requestCharset);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    static RequestBuilder newBuilder() {
        return new RequestBuilder();
    }
}
