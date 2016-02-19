package net.dongliu.requests;

import net.dongliu.requests.ResponseHandler;
import net.dongliu.requests.ResponseProcessor;
import net.dongliu.requests.struct.Headers;
import org.apache.http.HttpEntity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Dong Liu dongliu@live.cn
 */
public class ResponseHandlerAdapter<T> implements ResponseProcessor<T> {

    private final ResponseHandler<T> responseHandler;
    private static final byte[] emptyBytes = {};

    ResponseHandlerAdapter(ResponseHandler<T> responseHandler) {
        this.responseHandler = responseHandler;
    }

    @Override
    public T convert(int statusCode, Headers headers, HttpEntity httpEntity) throws IOException {
        if (httpEntity == null) {
            try (InputStream in = emptyInput()) {
                return responseHandler.handle(statusCode, headers, in);
            }
        }
        try (InputStream in = httpEntity.getContent()) {
            return responseHandler.handle(statusCode, headers, in);
        }
    }

    private InputStream emptyInput() {
        return new ByteArrayInputStream(emptyBytes);
    }
}
