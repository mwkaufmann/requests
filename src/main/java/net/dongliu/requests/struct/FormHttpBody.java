package net.dongliu.requests.struct;

import org.apache.http.annotation.Immutable;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.message.BasicNameValuePair;

import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Liu Dong
 */
@Immutable
public class FormHttpBody extends HttpBody<Collection<? extends Map.Entry<String, String>>> {
    public FormHttpBody(Collection<? extends Map.Entry<String, String>> body) {
        super(body);
    }

    @Override
    public AbstractHttpEntity createEntity(Charset charset) {
        // use www-form-urlencoded to send params
        List<BasicNameValuePair> list = getBody().stream()
                .map(nv -> new BasicNameValuePair(nv.getKey(), nv.getValue()))
                .collect(Collectors.toList());
        return new UrlEncodedFormEntity(list, charset);
    }
}
