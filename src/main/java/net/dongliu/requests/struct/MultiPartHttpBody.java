package net.dongliu.requests.struct;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import java.nio.charset.Charset;
import java.util.Collection;

/**
 * @author Liu Dong
 */
public class MultiPartHttpBody extends HttpBody<Collection<? extends Part>> {
    public MultiPartHttpBody(Collection<? extends Part> body) {
        super(body);
    }

    @Override
    public HttpEntity createEntity(Charset charset) {
        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
        for (Part f : getBody()) {
            switch (f.getType()) {
                case TEXT:
                    entityBuilder.addTextBody(f.getName(), f.getValue());
                    break;
                case FILE:
                    entityBuilder.addBinaryBody(f.getName(), f.getFile(),
                            ContentType.create(f.getMime()), f.getFileName());
                    break;
                case STREAM:
                    entityBuilder.addBinaryBody(f.getName(), f.getIn(),
                            ContentType.create(f.getMime()), f.getFileName());
                    break;
                case BYTES:
                    entityBuilder.addBinaryBody(f.getName(), f.getBytes(),
                            ContentType.create(f.getMime()), f.getFileName());
                    break;
            }

        }
        return entityBuilder.build();
    }
}
