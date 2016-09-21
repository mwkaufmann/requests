package net.dongliu.requests;

import net.dongliu.requests.json.TypeInfer;
import net.dongliu.requests.body.Part;
import net.dongliu.requests.mock.MockServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
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
        String text = resp.readToText();
        assertTrue(text.isEmpty());
    }

    @Test
    public void testPost() {
        // form encoded post
        String text = Requests.post("http://127.0.0.1:8080/post")
                .forms(Parameter.of("wd", "test"))
                .send().readToText();
        assertTrue(text.contains("wd=test"));
    }

    @Test
    public void testCookie() {
        RawResponse response = Requests.get("http://127.0.0.1:8080/cookie")
                .cookies(Parameter.of("test", "value")).send();
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
        String text = Requests.post("http://127.0.0.1:8080/echo_body").jsonBody(Arrays.asList(1, 2, 3))
                .send().readToText();
        assertTrue(text.startsWith("["));
        assertTrue(text.endsWith("]"));
    }

    @Test
    public void receiveJson() {
        List<Integer> list = Requests.post("http://127.0.0.1:8080/echo_body").jsonBody(Arrays.asList(1, 2, 3))
                .send().readToJson(new TypeInfer<List<Integer>>() {});
        assertEquals(3, list.size());
    }

    @Test
    public void sendHeaders() {
        String text = Requests.get("http://127.0.0.1:8080/echo_header")
                .headers(Parameter.of("Host", "www.baidu.com"), Parameter.of("TestHeader", 1))
                .send().readToText();
        assertTrue(text.contains("Host: www.baidu.com"));
        assertTrue(text.contains("TestHeader: 1"));
    }

    @Test
    public void testHttps() {
        RawResponse response = Requests.get("https://127.0.0.1:8443/https")
                .verify(false).send();
        String s = response.readToText();
        assertEquals(200, response.getStatusCode());
        assertFalse(s.isEmpty());
    }

    @Test
    public void testInterceptor() {
        long[] elapsed = {0};
        Interceptor interceptor = (target, request) -> {
            long start = System.nanoTime();
            RawResponse response = target.proceed(request);
            elapsed[0] = System.nanoTime() - start;
            return response;
        };
        String text = Requests.get("http://127.0.0.1:8080/echo_header")
                .interceptors(interceptor)
                .send().readToText();
        assertTrue(elapsed[0] > 0);
    }
}