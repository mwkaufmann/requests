package net.dongliu.requests;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

public interface IBaseRequestBuilder<T> {
    /**
     * Set params of url query string. Will overwrite old cookie values
     * This is for set parameters in url query str, If want to set post form params use form((Map&lt;String, ?&gt; params) method
     */
    default T params(Map<String, String> params) {
        return params(params.entrySet());
    }

    /**
     * Set params of url query string. Will overwrite old param values
     * This is for set parameters in url query str, If want to set post form params use form(Parameter... params) method
     */
    default T params(Map.Entry<String, String>... params) {
        return params(Arrays.asList(params));
    }

    /**
     * Set params of url query string. Will overwrite old param values
     * This is for set parameters in url query str, If want to set post form params use form(Parameter... params) method
     */
    T params(Collection<? extends Map.Entry<String, String>> params);

    /**
     * Set charset used to encode request, default utf-8.
     */
    T requestCharset(Charset charset);

    /**
     * Set charset used to encode request, default utf-8.
     */
    default T requestCharset(String charset) {
        return requestCharset(Charset.forName(charset));
    }

    /**
     * Set headers. Will overwrite old header values
     */
    default T headers(Map<String, String> headers) {
        return headers(headers.entrySet());
    }

    /**
     * Set headers. Will overwrite old header values
     */
    default T headers(Map.Entry<String, String>... headers) {
        return headers(Arrays.asList(headers));
    }

    /**
     * Set headers. Will overwrite old header values
     */
    T headers(Collection<? extends Map.Entry<String, String>> headers);

    /**
     * set http basic auth info
     */
    T basicAuth(String userName, String password);

    /**
     * Set cookies. Will overwrite old cookie values
     */
    default T cookies(Map<String, String> cookies) {
        return cookies(cookies.entrySet());
    }

    /**
     * Set cookies. Will overwrite old cookie values
     */
    default T cookies(Map.Entry<String, String>... cookies) {
        return cookies(Arrays.asList(cookies));
    }

    /**
     * Set cookies. Will overwrite old cookie values
     */
    T cookies(Collection<? extends Map.Entry<String, String>> cookies);

}
