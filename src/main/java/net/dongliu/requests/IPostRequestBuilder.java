package net.dongliu.requests;

import net.dongliu.requests.struct.FormHttpBody;
import net.dongliu.requests.struct.Part;
import net.dongliu.requests.struct.MultiPartHttpBody;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

public interface IPostRequestBuilder<T> extends IBodyRequestBuilder<T> {

    /**
     * Set http form body data, for http post method with form-encoded body.  Will overwrite old param values
     */
    default T forms(Map<String, String> map) {
        return body(new FormHttpBody(map.entrySet()));
    }

    /**
     * Set http form body data, for http post method with form-encoded body.
     */
    default T forms(Map.Entry<String, String>... parameters) {
        return body(new FormHttpBody(Arrays.asList(parameters)));
    }

    /**
     * Set http form body data, for http post method with form-encoded body.
     */
    default T forms(Collection<? extends Map.Entry<String, String>> parameters) {
        return body(new FormHttpBody(parameters));
    }

    /**
     * Set multi-part body, for http post method with multiPart body.
     */
    default T multiParts(Collection<? extends Part> multiParts) {
        return body(new MultiPartHttpBody(multiParts));
    }

    /**
     * Set multi-part body, for http post method with multiPart body.
     */
    default T multiParts(Part... multiParts) {
        return multiParts(Arrays.asList(multiParts));
    }
}
