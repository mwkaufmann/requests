package net.dongliu.requests;

import org.apache.http.Header;
import org.apache.http.entity.ContentType;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

/**
 * interface to trans data to result
 *
 * @author Dong Liu
 */
interface ResponseProcessor<T> {

    /**
     * from http Body to result with type T
     */
    T convert(ResponseData responseData) throws IOException;

    class ResponseData {
        private final int status;
        private final List<Header> headers;
        private final long contentLen;
        @Nullable
        private final ContentType contentType;
        private final InputStream in;

        public ResponseData(int status, List<Header> headers, long contentLen, ContentType contentType, InputStream in) {
            this.status = status;
            this.headers = headers;
            this.contentLen = contentLen;
            this.contentType = contentType;
            this.in = in;
        }

        public int getStatus() {
            return status;
        }

        public List<Header> getHeaders() {
            return headers;
        }

        /**
         * Http content len. if less than zero, means no len knows
         */
        public long getContentLen() {
            return contentLen;
        }

        @Nullable
        public ContentType getContentType() {
            return contentType;
        }

        public InputStream getIn() {
            return in;
        }

        /**
         * Get charset from content-type header; if not found use defaultCharset
         */
        public Charset getCharset(Charset defaultCharset) {
            Charset charset = null;
            ContentType contentType = getContentType();
            if (contentType != null) {
                charset = contentType.getCharset();
            }
            if (charset == null) {
                charset = defaultCharset;
            }
            return charset;
        }
    }
}
