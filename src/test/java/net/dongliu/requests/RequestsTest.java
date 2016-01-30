package net.dongliu.requests;

import net.dongliu.requests.mock.MockServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
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
        Response<String> resp = Requests.get("http://127.0.0.1:8080")
                .charset(StandardCharsets.UTF_8).text();
        assertEquals(200, resp.getStatusCode());

        resp = Requests.get("http://127.0.0.1:8080").text();
        assertEquals(200, resp.getStatusCode());

        // get with params
        Map<String, String> map = new HashMap<>();
        map.put("wd", "test");
        resp = Requests.get("http://127.0.0.1:8080").params(map).text();
        assertEquals(200, resp.getStatusCode());
        assertTrue(resp.getBody().contains("wd=test"));
    }

    @Test
    public void testPost() {
        // form encoded post
        Response<String> resp = Requests.post("http://127.0.0.1:8080/post")
                .addForm("wd", "test")
                .text();
        assertTrue(resp.getBody().contains("wd=test"));
    }

    @Test
    public void testCookie() {
        Response<String> response = Requests.get("http://127.0.0.1:8080/cookie")
                .addCookie("test", "value").text();
        assertNotNull(response.getCookies().getFirst("test"));
    }

    @Test
    public void testBasicAuth() {
        Response<String> response = Requests.get("http://127.0.0.1:8080/basicAuth")
                .auth("test", "password")
                .text();
        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void testRedirect() {
        Response<String> resp = Requests.get("http://127.0.0.1:8080/redirect").text();
        assertEquals(200, resp.getStatusCode());
        assertEquals("/", resp.getHistory().get(0).getPath());
    }

    @Test
    public void testMultiPart() {
        Response<String> response = Requests.post("http://127.0.0.1:8080/upload")
                .addMultiPart("file", "application/octem-stream", this.getClass().getResourceAsStream("/keystore"))
                .text();
        assertEquals(200, response.getStatusCode());
        assertTrue(response.getBody().contains("file"));
        assertTrue(response.getBody().contains("application/octem-stream"));
    }

    @Test
    public void testClientSetting() throws IOException {
        Response<String> response = Requests.get("https://127.0.0.1:8443/otn/")
                .timeout(3_000)
                .compress(false)
                .allowRedirects(false)
                .userAgent("Custom user agent")
                .verify(false).text();
        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void testHttps() throws IOException {
        Response<String> response = Requests.get("https://127.0.0.1:8443/otn/").verify(false).text();
        assertEquals(200, response.getStatusCode());
    }

}