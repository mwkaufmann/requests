package net.dongliu.requests;

import net.dongliu.requests.struct.Cookie;
import net.dongliu.requests.struct.Header;
import net.dongliu.requests.struct.Parameter;

import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface BaseRequestBuilderInterface<T> {
    /**
     * Set params of url query string. Will overwrite old cookie values
     * This is for set parameters in url query str, If want to set post form params use form((Map&lt;String, ?&gt; params) method
     */
    T params(Map<String, ?> params);

    /**
     * Set params of url query string. Will overwrite old param values
     * This is for set parameters in url query str, If want to set post form params use form(Parameter... params) method
     */
    T params(Parameter... params);

    /**
     * Set params of url query string. Will overwrite old param values
     * This is for set parameters in url query str, If want to set post form params use form(Parameter... params) method
     */
    T params(Collection<Parameter> params);

    /**
     * Add one parameter to url query string. Will overwrite old param values
     * This is for set parameters in url query str, If want to set post form params use form(String key, Object value) method
     */
    T addParam(String key, Object value);

    /**
     * Set charset used to encode request, default utf-8.
     */
    T charset(Charset charset);

    /**
     * Set charset used to encode request, default utf-8.
     */
    T charset(String charset);

    /**
     * Set headers. Will overwrite old header values
     */
    T headers(Map<String, ?> params);

    /**
     * Set headers. Will overwrite old header values
     */
    T headers(Header... headers);

    /**
     * Set headers. Will overwrite old header values
     */
    T headers(List<Header> headers);

    /**
     * Add one header
     */
    T addHeader(String key, Object value);

    /**
     * set http basic auth info
     */
    T auth(String userName, String password);

    /**
     * Set cookies. Will overwrite old cookie values
     */
    T cookies(Map<String, String> cookies);

    /**
     * Set cookies. Will overwrite old cookie values
     */
    T cookies(Cookie... cookies);

    /**
     * Set cookies. Will overwrite old cookie values
     */
    T cookies(Collection<Cookie> cookies);

    /**
     * Add one cookie
     */
    T addCookie(String name, String value);
}
