package net.dongliu.requests;

/**
 * @author Liu Dong
 */
public class BaseRequestBuilder extends RequestBuilder<BaseRequestBuilder> {
    @Override
    Request build() {
        return new Request(method, url, parameters, userAgent, headers, null, charset, authInfo, compress, verify, cookies,
                allowRedirects, connectTimeout, socketTimeout, proxy);
    }
}
