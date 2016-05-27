package net.dongliu.requests;

import net.dongliu.commons.collection.Lists;
import net.dongliu.commons.collection.Pair;
import net.dongliu.requests.body.Part;
import net.dongliu.requests.json.TypeInfer;
import net.dongliu.requests.mock.MockServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
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
                .requestCharset(StandardCharsets.UTF_8).send().readToText();
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
                .requestCharset(StandardCharsets.UTF_8).send();
        assertEquals(200, resp.getStatusCode());
        resp.readToText();
    }

    @Test
    public void testPost() {
        // form encoded post
        String text = Requests.post("http://127.0.0.1:8080/post")
                .forms(Pair.of("wd", "test"))
                .send().readToText();
        assertTrue(text.contains("wd=test"));
    }

    @Test
    public void testCookie() {
        RawResponse response = Requests.get("http://127.0.0.1:8080/cookie")
                .cookies(Pair.of("test", "value")).send();
        assertTrue(response.getCookies().stream().filter(c -> c.getName().equals("test")).findFirst().isPresent());
    }

    @Test
    public void testBasicAuth() {
        RawResponse response = Requests.get("http://127.0.0.1:8080/basicAuth")
                .basicAuth("test", "password")
                .send();
        assertEquals(200, response.getStatusCode());
        response.readToText();
    }

    @Test
    public void testRedirect() {
        RawResponse resp = Requests.get("http://127.0.0.1:8080/redirect").send();
        assertEquals(200, resp.getStatusCode());
//        assertEquals("/", resp.getRedirectLocations().get(0).getPath());
        resp.readToText();
    }

    @Test
    public void testMultiPart() {
        String response = Requests.post("http://127.0.0.1:8080/upload")
                .multiPartBody(Part.file("writeTo", "keystore", this.getClass().getResourceAsStream("/keystore"))
                        .contentType("application/octem-stream"))
                .send().readToText();
        assertTrue(response.contains("writeTo"));
        assertTrue(response.contains("application/octem-stream"));
    }

    @Test
    public void sendJson() {
        String text = Requests.post("http://127.0.0.1:8080/echo_body").jsonBody(Lists.of(1, 2, 3))
                .send().readToText();
        assertTrue(text.startsWith("["));
        assertTrue(text.endsWith("]"));
    }

    @Test
    public void receiveJson() {
        List<Integer> list = Requests.post("http://127.0.0.1:8080/echo_body").jsonBody(Lists.of(1, 2, 3))
                .send().readAsJson(new TypeInfer<List<Integer>>() {});
        assertEquals(3, list.size());
    }

    @Test
    public void sendHeaders() {
        String text = Requests.get("http://127.0.0.1:8080/echo_header")
                .headers(Pair.of("Host", "www.baidu.com"), Pair.of("TestHeader", 1))
                .send().readToText();
        assertTrue(text.contains("Host: www.baidu.com"));
        assertTrue(text.contains("TestHeader: 1"));
    }
}