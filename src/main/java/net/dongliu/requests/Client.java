package net.dongliu.requests;

import net.dongliu.requests.encode.URIBuilder;
import net.dongliu.requests.exception.RequestException;
import net.dongliu.requests.struct.*;
import org.apache.http.HttpEntity;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

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
    private final boolean closeOnRequestFinished;
    private final AtomicBoolean closed = new AtomicBoolean(false);

    public Client(CloseableHttpClient client, boolean closeOnRequestFinished) {
        this.client = client;
        this.closeOnRequestFinished = closeOnRequestFinished;
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
        if (closed.compareAndSet(false, true)) {
            try {
                client.close();
            } catch (IOException ignore) {
            }
        }
    }

    boolean getClosed() {
        return closed.get();
    }

    /**
     * get method
     */
    public HeadOnlyRequestBuilder get(String url) throws RequestException {
        return new HeadOnlyRequestBuilder().client(this).method(Method.GET).url(url);
    }

    /**
     * head method
     */
    public HeadOnlyRequestBuilder head(String url) throws RequestException {
        return new HeadOnlyRequestBuilder().client(this).method(Method.HEAD).url(url);
    }

    /**
     * get url, and return content
     */
    public PostRequestBuilder post(String url) throws RequestException {
        return new PostRequestBuilder().client(this).method(Method.POST).url(url);
    }

    /**
     * put method
     */
    public BodyRequestBuilder put(String url) throws RequestException {
        return new BodyRequestBuilder().client(this).method(Method.PUT).url(url);
    }

    /**
     * delete method
     */
    public HeadOnlyRequestBuilder delete(String url) throws RequestException {
        return new HeadOnlyRequestBuilder().client(this).method(Method.DELETE).url(url);
    }

    /**
     * options method
     */
    public HeadOnlyRequestBuilder options(String url) throws RequestException {
        return new HeadOnlyRequestBuilder().client(this).method(Method.OPTIONS).url(url);
    }

    /**
     * patch method
     */
    public BodyRequestBuilder patch(String url) throws RequestException {
        return new BodyRequestBuilder().client(this).method(Method.PATCH).url(url);
    }

    /**
     * trace method
     */
    public HeadOnlyRequestBuilder trace(String url) throws RequestException {
        return new HeadOnlyRequestBuilder().client(this).method(Method.TRACE).url(url);
    }

    /**
     * create a session. session can do request as Requests do, and keep cookies to maintain a http session
     */
    public Session session() {
        return new Session(this);
    }

    CloseableHttpClient getHttpClient() {
        return client;
    }

    /**
     * execute request, get http response, and convert response with processor
     */
    <T> Response<T> execute(Request request, ResponseProcessor<T> processor, Session session) throws RequestException {
        CredentialsProvider provider = new BasicCredentialsProvider();
        HttpClientContext context;
        if (session != null) {
            context = session.getContext();
        } else {
            context = HttpClientContext.create();
            CookieStore cookieStore = new BasicCookieStore();
            context.setCookieStore(cookieStore);
        }

        HttpRequestBase httpRequest = buildRequest(request, context);
        // basic auth
        if (request.getAuthInfo() != null) {
            UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(
                    request.getAuthInfo().getUserName(), request.getAuthInfo().getPassword());
            provider.setCredentials(new AuthScope(request.getUrl().getHost(), request.getUrl().getPort()), credentials);
        }

        context.setAttribute(HttpClientContext.CREDS_PROVIDER, provider);
        // do http request with http client
        try (CloseableHttpResponse httpResponse = client.execute(httpRequest, context)) {
            return wrapResponse(httpResponse, context, processor);
        } catch (IOException e) {
            throw new RequestException(e);
        } finally {
            if (closeOnRequestFinished) {
                close();
            }
        }
    }

    // build http request
    private HttpRequestBase buildRequest(Request request, HttpClientContext context) {
        URI uri = joinFullUrl(request.getUrl(), request.getCharset(), request.getParameters());
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
            for (Cookie cookie : request.getCookies()) {
                BasicClientCookie clientCookie = new BasicClientCookie(cookie.getName(),
                        cookie.getValue());
                clientCookie.setDomain(request.getUrl().getHost());
                clientCookie.setPath("/");
                cookieStore.addCookie(clientCookie);
            }
        }

        // set headers
        if (request.getHeaders() != null) {
            for (Header header : request.getHeaders()) {
                httpRequest.setHeader(header.getName(), header.getValue());
            }
        }

        return httpRequest;
    }

    private HttpRequestBase buildHttpPut(URI uri, Request request) {
        HttpPut httpPut = new HttpPut(uri);
        HttpBody body = request.getHttpBody();
        if (request.getHttpBody() != null) {
            if (body instanceof StringHttpBody) {
                httpPut.setEntity(new StringEntity(((StringHttpBody) body).getBody(), request.getCharset()));
            } else if (body instanceof BytesHttpBody) {
                httpPut.setEntity(new ByteArrayEntity(((BytesHttpBody) body).getBody()));
            } else if (body instanceof InputHttpBody) {
                httpPut.setEntity(new InputStreamEntity(((InputHttpBody) body).getBody()));
            }
        }
        return httpPut;
    }


    private HttpPost buildHttpPost(URI uri, Request request) {
        HttpPost httpPost = new HttpPost(uri);
        HttpBody body = request.getHttpBody();
        if (body instanceof MultiPartHttpBody) {
            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
            for (MultiPart f : ((MultiPartHttpBody) body).getBody()) {
                switch (f.getType()) {
                    case TEXT:
                        entityBuilder.addTextBody(f.getName(), f.getValue());
                        break;
                    case FILE:
                        entityBuilder.addBinaryBody(f.getName(), f.getFile(),
                                ContentType.create(f.getMime()), f.getFileName());
                        break;
                    case STREAM:
                        entityBuilder.addBinaryBody(f.getName(), f.getIn(),
                                ContentType.create(f.getMime()), f.getFileName());
                        break;
                    case BYTES:
                        entityBuilder.addBinaryBody(f.getName(), f.getBytes(),
                                ContentType.create(f.getMime()), f.getFileName());
                        break;
                }

            }
            httpPost.setEntity(entityBuilder.build());
        } else if (body instanceof StringHttpBody) {
            httpPost.setEntity(new StringEntity(((StringHttpBody) body).getBody(), request.getCharset()));
        } else if (body instanceof BytesHttpBody) {
            httpPost.setEntity(new ByteArrayEntity(((BytesHttpBody) body).getBody()));
        } else if (body instanceof InputHttpBody) {
            httpPost.setEntity(new InputStreamEntity(((InputHttpBody) body).getBody()));
        } else if (body instanceof FormHttpBody) {
            // use www-form-urlencoded to send params
            List<Parameter> forms = ((FormHttpBody) body).getBody();
            List<BasicNameValuePair> paramList = new ArrayList<>(forms.size());
            for (Parameter param : forms) {
                paramList.add(new BasicNameValuePair(param.getName(), param.getValue()));
            }
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList, request.getCharset());
            httpPost.setEntity(entity);
        }
        return httpPost;
    }


    private HttpRequestBase buildHttpPatch(URI uri, Request request) {
        HttpPatch httpPatch = new HttpPatch(uri);
        HttpBody body = request.getHttpBody();
        if (request.getHttpBody() != null) {
            if (body instanceof StringHttpBody) {
                httpPatch.setEntity(new StringEntity(((StringHttpBody) body).getBody(), request.getCharset()));
            } else if (body instanceof BytesHttpBody) {
                httpPatch.setEntity(new ByteArrayEntity(((BytesHttpBody) body).getBody()));
            } else if (body instanceof InputHttpBody) {
                httpPatch.setEntity(new InputStreamEntity(((InputHttpBody) body).getBody()));
            }
        }
        return httpPatch;
    }

    private URI joinFullUrl(URI url, Charset charset, List<Parameter> parameters) {
        try {
            if (parameters == null || parameters.isEmpty()) {
                return url;
            }
            URIBuilder urlBuilder = new URIBuilder(url).setCharset(charset);
            for (Parameter param : parameters) {
                urlBuilder.addParameter(param.getName(), param.getValue());
            }
            return urlBuilder.build();
        } catch (URISyntaxException e) {
            throw new RequestException(e);
        }
    }

    /**
     * do http request with http client
     */
    private <T> Response<T> wrapResponse(CloseableHttpResponse httpResponse, HttpClientContext context,
                                         ResponseProcessor<T> processor) throws IOException {
        Response<T> response = new Response<>();
        response.setStatusCode(httpResponse.getStatusLine().getStatusCode());
        // get headers
        org.apache.http.Header[] respHeaders = httpResponse.getAllHeaders();
        Headers headers = new Headers();
        for (org.apache.http.Header header : respHeaders) {
            headers.add(new Header(header.getName(), header.getValue()));
        }
        response.setHeaders(headers);

        // get cookies
        Cookies cookies = new Cookies();
        for (org.apache.http.cookie.Cookie c : context.getCookieStore().getCookies()) {
            Cookie cookie = new Cookie(c.getName(), c.getValue());
            cookie.setPath(c.getPath());
            cookie.setDomain(c.getDomain());
            cookie.setPath(c.getPath());
            cookie.setExpiry(c.getExpiryDate());
            cookies.add(cookie);
        }
        response.setCookies(cookies);

        response.setHistory(context.getRedirectLocations());

        HttpEntity entity = httpResponse.getEntity();
        T result = processor.convert(response.getStatusCode(), headers, entity);
        response.setBody(result);
        return response;
    }

}
