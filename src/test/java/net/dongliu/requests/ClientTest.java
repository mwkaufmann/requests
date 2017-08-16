package net.dongliu.requests;

import net.dongliu.requests.mock.MockServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.URL;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

public class ClientTest {

    private static MockServer server = new MockServer();

    @BeforeClass
    public static void init() {
        server.start();
    }

    @AfterClass
    public static void destroy() {
        server.stop();
    }

    @Test
    public void newRequest() throws Exception {
        Client client = Client.Builder().userAgent("the useragent").create();
        PlainRequestBuilder builder = client.newRequest("GET", "http://www.test.com");
        assertEquals("the useragent", builder.userAgent);
        assertEquals("GET", builder.method);
        assertEquals(new URL("http://www.test.com"), builder.url);
    }

    @Test
    public void get() throws Exception {
        Client client = Client.newClient();
        Response<String> resp = client.get("http://127.0.0.1:8080")
                .requestCharset(StandardCharsets.UTF_8).send().toTextResponse();
        assertEquals(200, resp.getStatusCode());
        assertFalse(resp.getBody().isEmpty());
    }
}