package net.dongliu.requests.json;

import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;

/**
 * Json provider
 *
 * @author Liu Dong
 */
public interface JsonProvider {

    /**
     * Serialize value to json, and write to writer
     */
    void marshal(Writer writer, Object value);

    /**
     * Deserialize json from reader
     */
    <T> T unmarshal(Reader reader, Type type);
}
