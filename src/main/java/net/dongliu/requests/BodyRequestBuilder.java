package net.dongliu.requests;

/**
 * @author Liu Dong
 */
public class BodyRequestBuilder extends AbstractBodyRequestBuilder<BodyRequestBuilder> {
    @Override
    Request build() {
        return new Request(method, url, parameters, userAgent, headers, httpBody, charset, authInfo, gzip, verify,
                cookies, allowRedirects, allowPostRedirects,
                connectTimeout, socketTimeout, proxy);
    }
}
