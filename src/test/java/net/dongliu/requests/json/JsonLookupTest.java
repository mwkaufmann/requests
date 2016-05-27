package net.dongliu.requests.json;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Liu Dong
 */
public class JsonLookupTest {
    @Test
    public void hashGson() throws Exception {
        JsonLookup jsonLookup = JsonLookup.getInstance();
        assertTrue(jsonLookup.hasGson());
    }

    @Test
    public void hashJackson() throws Exception {
        JsonLookup jsonLookup = JsonLookup.getInstance();
        assertTrue(jsonLookup.hasJackson());
    }
}