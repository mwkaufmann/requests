package net.dongliu.requests.json;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

/**
 * Json provider
 *
 * @author Liu Dong
 */
public interface JsonProvider {

    /**
     * Serialize value to json, and write to writer
     */
    void marshal(Writer writer, @Nullable Object value) throws IOException;

    /**
     * Deserialize json from reader, with type
     */
    @Nullable
    <T> T unmarshal(Reader reader, Type type) throws IOException;

    /**
     * Deserialize json from input stream, with charset and type.
     * This method is for FastJson due it has no api to unmarshal from Reader
     */
    @Nullable
    <T> T unmarshal(InputStream inputStream, Charset charset, Type type) throws IOException;

}
