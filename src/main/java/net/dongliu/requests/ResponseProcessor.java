package net.dongliu.requests;

import org.apache.http.Header;
import org.apache.http.HttpEntity;

import java.io.IOException;
import java.util.List;

/**
 * interface to trans data to result
 *
 * @author Dong Liu
 */
interface ResponseProcessor<T> {

    /**
     * from http Body to result with type T
     *
     * @param httpEntity may be null if no http entity is set
     */
    T convert(int status, List<Header> headers, HttpEntity httpEntity) throws IOException;
}
