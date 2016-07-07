package net.dongliu.requests;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * @author Liu Dong
 */
public class ResponseHeadersTest {
    @Test
    public void getHeaders() throws Exception {
        ResponseHeaders headers = new ResponseHeaders(Arrays.asList(
                Pair.of("Location", "www"),
                Pair.of("Location", "www2"),
                Pair.of("Content-Length", "100")
        ));
        assertEquals(Arrays.asList("www", "www2"), headers.getHeaders("Location"));
    }

    @Test
    public void getFirstHeader() throws Exception {
        ResponseHeaders headers = new ResponseHeaders(Arrays.asList(
                Pair.of("Location", "www"),
                Pair.of("Location", "www2"),
                Pair.of("Content-Length", "100")
        ));
        assertEquals("www", headers.getFirstHeader("Location"));
        assertEquals("www", headers.getFirstHeader("location"));
    }

    @Test
    public void getLongHeader() throws Exception {
        ResponseHeaders headers = new ResponseHeaders(Arrays.asList(
                Pair.of("Location", "www"),
                Pair.of("Location", "www2"),
                Pair.of("Content-Length", "100")
        ));
        assertEquals(100, headers.getLongHeader("Content-Length", -1));
    }

}