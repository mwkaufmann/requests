package net.dongliu.requests;

import net.dongliu.requests.mock.MockServer;
import net.dongliu.requests.struct.Part;
import net.dongliu.requests.struct.Entry;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class RequestsTest {

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
    public void testGet() throws Exception {
        String resp = Requests.get("http://127.0.0.1:8080")
                .charset(StandardCharsets.UTF_8).send().readToText();
        assertFalse(resp.isEmpty());

        resp = Requests.get("http://127.0.0.1:8080").send().readToText();
        assertFalse(resp.isEmpty());

        // get with params
        Map<String, String> map = new HashMap<>();
        map.put("wd", "test");
        resp = Requests.get("http://127.0.0.1:8080").params(map).send().readToText();
        assertFalse(resp.isEmpty());
        assertTrue(resp.contains("wd=test"));
    }

    @Test
    public void testHead() {
        RawResponse resp = Requests.head("http://127.0.0.1:8080")
                .charset(StandardCharsets.UTF_8).send();
        assertEquals(200, resp.getStatus());
        resp.readToText();
    }

    @Test
    public void testPost() {
        // form encoded post
        String resp = Requests.post("http://127.0.0.1:8080/post")
                .forms(Entry.of("wd", "test"))
                .send().readToText();
        assertTrue(resp.contains("wd=test"));
    }

    @Test
    public void testCookie() {
        RawResponse response = Requests.get("http://127.0.0.1:8080/cookie")
                .cookies(Entry.of("test", "value")).send();
        assertTrue(response.getCookies().stream().filter(c -> c.getName().equals("test")).findFirst().isPresent());
    }

    @Test
    public void testBasicAuth() {
        RawResponse response = Requests.get("http://127.0.0.1:8080/basicAuth")
                .basicAuth("test", "password")
                .send();
        assertEquals(200, response.getStatus());
        response.readToText();
    }

    @Test
    public void testRedirect() {
        RawResponse resp = Requests.get("http://127.0.0.1:8080/redirect").send();
        assertEquals(200, resp.getStatus());
        assertEquals("/", resp.getHistory().get(0).getPath());
        resp.readToText();
    }

    @Test
    public void testMultiPart() {
        String response = Requests.post("http://127.0.0.1:8080/upload")
                .multiParts(new Part("writeTo", "application/octem-stream", "",
                        this.getClass().getResourceAsStream("/keystore")))
                .send().readToText();
        assertTrue(response.contains("writeTo"));
        assertTrue(response.contains("application/octem-stream"));
    }

    @Test
    public void testClientSetting() throws IOException {
        String response = Requests.get("https://127.0.0.1:8443/otn/")
                .timeout(3_000)
                .compress(false)
                .allowRedirects(false)
                .userAgent("Custom user agent")
                .verify(false).send().readToText();
    }

    @Test
    public void testHttps() throws IOException {
        String response = Requests.get("https://127.0.0.1:8443/otn/").verify(false).send().readToText();
    }

}