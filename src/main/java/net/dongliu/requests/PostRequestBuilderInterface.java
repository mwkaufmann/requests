package net.dongliu.requests;

import net.dongliu.requests.struct.Parameter;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

public interface PostRequestBuilderInterface<T> extends BodyRequestBuilderInterface<T> {
    /**
     * Add http form body data, for http post method with form-encoded body.
     */
    T addForm(String key, Object value);

    /**
     * Set http form body data, for http post method with form-encoded body.  Will overwrite old param values
     */
    T forms(Map<String, ?> map);

    /**
     * Set http form body data, for http post method with form-encoded body.
     */
    T forms(Parameter... parameters);

    /**
     * Set http form body data, for http post method with form-encoded body.
     */
    T forms(Collection<Parameter> parameters);

    /**
     * add multi part file, will send multiPart requests.
     *
     * @param file the file to be send
     */
    T addMultiPart(File file);

    /**
     * add multi part file, will send multiPart requests.
     *
     * @param name the http request field name for this file
     * @param file the file to be send
     */
    T addMultiPart(String name, File file);

    /**
     * add multi part file, will send multiPart requests.
     * this should be used with post method
     *
     * @param name     the http request field name for this file
     * @param mimeType the mimeType for file
     * @param file     the file to be send
     */
    T addMultiPart(String name, String mimeType, File file);

    /**
     * add multi part field by byte array, will send multiPart requests.
     *
     * @param name  the http request field name for this field
     * @param bytes the multipart request content
     */
    T addMultiPart(String name, String mimeType, byte[] bytes);

    /**
     * add multi part field by byte array, will send multiPart requests.
     *
     * @param name     the http request field name for this field
     * @param fileName the file name. can be null
     * @param bytes    the multipart request content
     */
    T addMultiPart(String name, String mimeType, String fileName, byte[] bytes);

    /**
     * add multi part field by inputStream, will send multiPart requests.
     *
     * @param name     the http request field name for this field
     * @param mimeType the mimeType for file
     * @param in       the inputStream for the content
     */
    T addMultiPart(String name, String mimeType, InputStream in);

    /**
     * add multi part field by inputStream, will send multiPart requests.
     *
     * @param name     the http request field name for this field
     * @param mimeType the mimeType for file
     * @param fileName the file name. can be null
     * @param in       the inputStream for the content
     */
    T addMultiPart(String name, String mimeType, String fileName, InputStream in);

    /**
     * Add multi part key-value parameter.
     *
     * @param name  the http request field name
     * @param value the file to be send
     */
    T addMultiPart(String name, String value);
}
