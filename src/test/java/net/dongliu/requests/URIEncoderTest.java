package net.dongliu.requests;

import net.dongliu.commons.collection.Lists;
import org.junit.Test;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class URIEncoderTest {
    @Test
    public void joinUrl() throws Exception {
        List<Map.Entry<String, String>> empty = Lists.of();
        assertEquals("http://www.test.com/", URIEncoder.joinUrl(new URL("http://www.test.com/"),
                empty, StandardCharsets.UTF_8).toExternalForm());
        assertEquals("http://www.test.com/path", URIEncoder.joinUrl(new URL("http://www.test.com/path"),
                empty, StandardCharsets.UTF_8).toExternalForm());

        assertEquals("http://www.test.com/path?t=v", URIEncoder.joinUrl(new URL("http://www.test.com/path"),
                Lists.of(Parameter.of("t", "v")), StandardCharsets.UTF_8).toExternalForm());
        assertEquals("http://www.test.com/path?s=t&t=v", URIEncoder.joinUrl(new URL("http://www.test.com/path?s=t"),
                Lists.of(Parameter.of("t", "v")), StandardCharsets.UTF_8).toExternalForm());
        assertEquals("http://www.test.com/path?t=v", URIEncoder.joinUrl(new URL("http://www.test.com/path?"),
                Lists.of(Parameter.of("t", "v")), StandardCharsets.UTF_8).toExternalForm());
        assertEquals("http://www.test.com/path?t=v#seg", URIEncoder.joinUrl(new URL("http://www.test.com/path#seg"),
                Lists.of(Parameter.of("t", "v")), StandardCharsets.UTF_8).toExternalForm());
        assertEquals("http://www.test.com/path?t=v#", URIEncoder.joinUrl(new URL("http://www.test.com/path#"),
                Lists.of(Parameter.of("t", "v")), StandardCharsets.UTF_8).toExternalForm());
    }

}