package net.dongliu.requests.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;

/**
 * Provider json ability with gson
 *
 * @author Liu Dong
 */
public class GsonProvider implements JsonProvider {

    private final Gson gson;

    public GsonProvider() {
        this(new GsonBuilder().disableHtmlEscaping().create());
    }

    public GsonProvider(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void marshal(Writer writer, Object value) {
        gson.toJson(value, writer);
    }

    @Override
    public <T> T unmarshal(Reader reader, Type type) {
        return gson.fromJson(reader, type);
    }
}
