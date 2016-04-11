package net.dongliu.requests;

import net.dongliu.requests.exception.UncheckedURISyntaxException;
import net.dongliu.requests.struct.AuthInfo;
import net.dongliu.requests.struct.HttpBody;
import net.dongliu.requests.struct.Method;
import net.dongliu.requests.struct.Proxy;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;

import javax.annotation.Nullable;
import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Pooled http client use connection pool, for reusing http connection across http requests.
 * This class is thread-safe, can service connection requests from multiple execution threads.
 *
 * @author Dong Liu dongliu@live.cn
 */
public class Client implements Closeable {

    // the wrapped http client
    private final CloseableHttpClient client;
    // close client when request finished
    private final boolean closeOnFinished;
    @Nullable
    private final Proxy proxy;
    private AuthInfo authInfo;

    public Client(CloseableHttpClient client, boolean closeOnFinished, Proxy proxy) {
        this.client = client;
        this.closeOnFinished = closeOnFinished;
        this.proxy = proxy;
    }

    /**
     * create new Connection-Pooled thread safe client Builder.
     */
    public static PooledClientBuilder pooled() {
        return new PooledClientBuilder();
    }

    /**
     * create un-thread-safe single connection client builder
     */
    public static SingleClientBuilder single() {
        return new SingleClientBuilder();
    }

    @Override
    public void close() {
        try {
            client.close();
        } catch (IOException ignore) {
        }
    }

    /**
     * get method
     */
    public HeadOnlyRequestBuilder get(String url) throws UncheckedIOException {
        return new HeadOnlyRequestBuilder().client(this).method(Method.GET).url(url);
    }

    /**
     * head method
     */
    public HeadOnlyRequestBuilder head(String url) throws UncheckedIOException {
        return new HeadOnlyRequestBuilder().client(this).method(Method.HEAD).url(url);
    }

    /**
     * get url, and return content
     */
    public PostRequestBuilder post(String url) throws UncheckedIOException {
        return new PostRequestBuilder().client(this).method(Method.POST).url(url);
    }

    /**
     * put method
     */
    public BodyRequestBuilder put(String url) throws UncheckedIOException {
        return new BodyRequestBuilder().client(this).method(Method.PUT).url(url);
    }

    /**
     * delete method
     */
    public HeadOnlyRequestBuilder delete(String url) throws UncheckedIOException {
        return new HeadOnlyRequestBuilder().client(this).method(Method.DELETE).url(url);
    }

    /**
     * options method
     */
    public HeadOnlyRequestBuilder options(String url) throws UncheckedIOException {
        return new HeadOnlyRequestBuilder().client(this).method(Method.OPTIONS).url(url);
    }

    /**
     * patch method
     */
    public BodyRequestBuilder patch(String url) throws UncheckedIOException {
        return new BodyRequestBuilder().client(this).method(Method.PATCH).url(url);
    }

    /**
     * trace method
     */
    public HeadOnlyRequestBuilder trace(String url) throws UncheckedIOException {
        return new HeadOnlyRequestBuilder().client(this).method(Method.TRACE).url(url);
    }

    /**
     * create a session. session can do request as Requests do, and keep cookies to maintain a http session
     */
    public Session session() {
        return new Session(this);
    }

    /**
     * execute request, get http response, and convert response with processor
     */
    RawResponse execute(Request request, @Nullable Session session) throws UncheckedIOException {
        HttpClientContext context;
        if (session != null) {
            context = session.getContext();
        } else {
            context = HttpClientContext.create();
            CookieStore cookieStore = new BasicCookieStore();
            context.setCookieStore(cookieStore);
        }

        // set authes
        CredentialsProvider provider = new BasicCredentialsProvider();
        HttpRequestBase httpRequest = buildRequest(request, context);
        // basic auth
        if (request.getAuthInfo() != null) {
            UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(
                    request.getAuthInfo().getUserName(), request.getAuthInfo().getPassword());
            provider.setCredentials(new AuthScope(request.getUrl().getHost(), request.getUrl().getPort()), credentials);
        }
        // proxy auth
        if (proxy != null && proxy.getScheme().equals(Proxy.HTTP)) {
            AuthInfo proxyAuthInfo = proxy.getAuthInfo();
            if (proxyAuthInfo != null) {
                provider.setCredentials(new AuthScope(proxy.getHost(), proxy.getPort()),
                        new UsernamePasswordCredentials(proxyAuthInfo.getUserName(), proxyAuthInfo.getUserName()));

            }
        }

        context.setAttribute(HttpClientContext.CREDS_PROVIDER, provider);
        // do http request with http client
        CloseableHttpResponse httpResponse;
        try {
            httpResponse = client.execute(httpRequest, context);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        int statusCode = httpResponse.getStatusLine().getStatusCode();
        // get headers
        List<Header> headers = Arrays.asList(httpResponse.getAllHeaders());

        // get cookies
        List<Cookie> cookies = context.getCookieStore().getCookies();

        List<URI> history = context.getRedirectLocations();

        return new RawResponse(httpResponse, this, closeOnFinished, statusCode, headers, cookies, history);
    }

    // build http request
    private HttpRequestBase buildRequest(Request request, HttpClientContext context) {
        URI uri = buildUrl(request.getUrl(), request.getCharset(), request.getParameters());
        HttpRequestBase httpRequest;
        switch (request.getMethod()) {
            case POST:
                httpRequest = buildHttpPost(uri, request);
                break;
            case GET:
                httpRequest = new HttpGet(uri);
                break;
            case HEAD:
                httpRequest = new HttpHead(uri);
                break;
            case PUT:
                httpRequest = buildHttpPut(uri, request);
                break;
            case DELETE:
                httpRequest = new HttpDelete(uri);
                break;
            case OPTIONS:
                httpRequest = new HttpOptions(uri);
                break;
            case TRACE:
                httpRequest = new HttpTrace(uri);
                break;
            case PATCH:
                httpRequest = buildHttpPatch(uri, request);
                break;
            case CONNECT:
            default:
                throw new UnsupportedOperationException("Unsupported method:" + request.getMethod());
        }

        // set cookie
        CookieStore cookieStore = context.getCookieStore();
        if (request.getCookies() != null) {
            for (Map.Entry<String, String> cookie : request.getCookies()) {
                BasicClientCookie clientCookie = new BasicClientCookie(cookie.getKey(),
                        cookie.getValue());
                clientCookie.setDomain(request.getUrl().getHost());
                clientCookie.setPath("/");
                cookieStore.addCookie(clientCookie);
            }
        }

        // set headers
        if (request.getHeaders() != null) {
            for (Map.Entry<String, String> header : request.getHeaders()) {
                httpRequest.setHeader(header.getKey(), header.getValue());
            }
        }

        return httpRequest;
    }

    private HttpRequestBase buildHttpPut(URI uri, Request request) {
        HttpPut httpPut = new HttpPut(uri);
        HttpBody body = request.getHttpBody();
        if (body != null) {
            HttpEntity entity = body.createEntity(request.getCharset());
            httpPut.setEntity(entity);
        }
        return httpPut;
    }


    private HttpPost buildHttpPost(URI uri, Request request) {
        HttpPost httpPost = new HttpPost(uri);
        HttpBody body = request.getHttpBody();
        if (body != null) {
            HttpEntity entity = body.createEntity(request.getCharset());
            httpPost.setEntity(entity);
        }
        return httpPost;
    }


    private HttpRequestBase buildHttpPatch(URI uri, Request request) {
        HttpPatch httpPatch = new HttpPatch(uri);
        HttpBody body = request.getHttpBody();
        if (body != null) {
            HttpEntity entity = body.createEntity(request.getCharset());
            httpPatch.setEntity(entity);
        }
        return httpPatch;
    }

    private URI buildUrl(URI url, Charset charset,
                         @Nullable Collection<? extends Map.Entry<String, String>> parameters) {
        try {
            if (parameters == null || parameters.isEmpty()) {
                return url;
            }
            URIBuilder urlBuilder = new URIBuilder(url).setCharset(charset);
            for (Map.Entry<String, String> param : parameters) {
                urlBuilder.addParameter(param.getKey(), param.getValue());
            }
            return urlBuilder.build();
        } catch (URISyntaxException e) {
            throw new UncheckedURISyntaxException(e);
        }
    }

}
