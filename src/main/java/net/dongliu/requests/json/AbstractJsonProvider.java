package net.dongliu.requests.json;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

/**
 * @author Liu Dong
 */
public abstract class AbstractJsonProvider implements JsonProvider {

    @Override
    @Nullable
    public <T> T unmarshal(InputStream inputStream, Charset charset, Type type) throws IOException {
        try (Reader reader = new InputStreamReader(inputStream, charset)) {
            return unmarshal(reader, type);
        }
    }
}
