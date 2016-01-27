package net.dongliu.requests;

import net.dongliu.requests.struct.FormHttpBody;
import net.dongliu.requests.struct.MultiPart;
import net.dongliu.requests.struct.MultiPartHttpBody;
import net.dongliu.requests.struct.Parameter;

import java.io.File;
import java.io.InputStream;
import java.util.*;

/**
 * @author Liu Dong
 */
public class PostRequestBuilder extends AbstractBodyRequestBuilder<PostRequestBuilder> {

    protected PostRequestBuilder() {
    }

    @Override
    public Request build() {
        return new Request(method, url, parameters, headers, httpBody, charset, authInfo, cookies);
    }

    @Override
    protected PostRequestBuilder self() {
        return this;
    }

    /**
     * Add http form body data, for http post method with form-encoded body.
     */
    public PostRequestBuilder addForm(String key, Object value) {
        List<Parameter> params = ensureFormParameters();
        params.add(Parameter.of(key, value));
        return this;
    }

    /**
     * Set http form body data, for http post method with form-encoded body.  Will overwrite old param values
     */
    public PostRequestBuilder forms(Map<String, ?> map) {
        checkHttpBody(FormHttpBody.class);
        List<Parameter> params = new ArrayList<>();
        for (Map.Entry<String, ?> e : map.entrySet()) {
            params.add(Parameter.of(e.getKey(), e.getValue()));
        }
        this.httpBody = new FormHttpBody(params);
        return this;
    }

    /**
     * Set http form body data, for http post method with form-encoded body.
     */
    public PostRequestBuilder forms(Parameter... parameters) {
        checkHttpBody(FormHttpBody.class);
        List<Parameter> params = new ArrayList<>();
        Collections.addAll(params, parameters);
        this.httpBody = new FormHttpBody(params);
        return this;
    }

    /**
     * Set http form body data, for http post method with form-encoded body.
     */
    public PostRequestBuilder forms(Collection<Parameter> parameters) {
        checkHttpBody(FormHttpBody.class);
        List<Parameter> params = new ArrayList<>();
        params.addAll(parameters);
        this.httpBody = new FormHttpBody(params);
        return this;
    }

    private List<Parameter> ensureFormParameters() {
        checkHttpBody(FormHttpBody.class);
        if (this.httpBody == null) {
            this.httpBody = new FormHttpBody(new ArrayList<Parameter>());
        }
        return ((FormHttpBody) this.httpBody).getBody();
    }

    /**
     * add multi part file, will send multiPart requests.
     *
     * @param file the file to be send
     */
    public PostRequestBuilder addMultiPart(File file) {
        List<MultiPart> mps = ensureMultiPart();
        mps.add(new MultiPart(file.getName(), file));
        return this;
    }

    /**
     * add multi part file, will send multiPart requests.
     *
     * @param name the http request field name for this file
     * @param file the file to be send
     */
    public PostRequestBuilder addMultiPart(String name, File file) {
        List<MultiPart> mps = ensureMultiPart();
        mps.add(new MultiPart(name, file));
        return this;
    }

    /**
     * add multi part file, will send multiPart requests.
     * this should be used with post method
     *
     * @param name     the http request field name for this file
     * @param mimeType the mimeType for file
     * @param file     the file to be send
     */
    public PostRequestBuilder addMultiPart(String name, String mimeType, File file) {
        List<MultiPart> mps = ensureMultiPart();
        mps.add(new MultiPart(name, mimeType, file));
        return this;
    }

    /**
     * add multi part field by byte array, will send multiPart requests.
     *
     * @param name  the http request field name for this field
     * @param bytes the multipart request content
     */
    public PostRequestBuilder addMultiPart(String name, String mimeType, byte[] bytes) {
        addMultiPart(name, mimeType, null, bytes);
        return this;
    }

    /**
     * add multi part field by byte array, will send multiPart requests.
     *
     * @param name     the http request field name for this field
     * @param fileName the file name. can be null
     * @param bytes    the multipart request content
     */
    public PostRequestBuilder addMultiPart(String name, String mimeType, String fileName, byte[] bytes) {
        List<MultiPart> mps = ensureMultiPart();
        mps.add(new MultiPart(name, mimeType, fileName, bytes));
        return this;
    }

    /**
     * add multi part field by inputStream, will send multiPart requests.
     *
     * @param name     the http request field name for this field
     * @param mimeType the mimeType for file
     * @param in       the inputStream for the content
     */
    public PostRequestBuilder addMultiPart(String name, String mimeType, InputStream in) {
        addMultiPart(name, mimeType, null, in);
        return this;
    }

    /**
     * add multi part field by inputStream, will send multiPart requests.
     *
     * @param name     the http request field name for this field
     * @param mimeType the mimeType for file
     * @param fileName the file name. can be null
     * @param in       the inputStream for the content
     */
    public PostRequestBuilder addMultiPart(String name, String mimeType, String fileName, InputStream in) {
        List<MultiPart> mps = ensureMultiPart();
        mps.add(new MultiPart(name, mimeType, fileName, in));
        return this;
    }

    /**
     * Add multi part key-value parameter.
     *
     * @param name  the http request field name
     * @param value the file to be send
     */
    public PostRequestBuilder addMultiPart(String name, String value) {
        List<MultiPart> mps = ensureMultiPart();
        mps.add(new MultiPart(name, value));
        return this;
    }

    private List<MultiPart> ensureMultiPart() {
        checkHttpBody(MultiPartHttpBody.class);
        if (this.httpBody == null) {
            this.httpBody = new MultiPartHttpBody(new ArrayList<MultiPart>());
        }
        return ((MultiPartHttpBody) this.httpBody).getBody();
    }
}
