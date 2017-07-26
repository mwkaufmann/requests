package net.dongliu.requests;

import net.dongliu.requests.body.Part;
import net.dongliu.requests.json.TypeInfer;
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
        String statusLine = resp.getStatusLine();
        assertEquals("HTTP/1.1 200 OK", statusLine);
        String text = resp.readToText();
        assertTrue(text.isEmpty());
    }

    @Test
    public void testPost() {
        // form encoded post
        String text = Requests.post("http://127.0.0.1:8080/post")
                .body(Parameter.of("wd", "test"))
                .send().readToText();
        assertTrue(text.contains("wd=test"));
    }

    @Test
    public void testCookie() {
        Response<String> response = Requests.get("http://127.0.0.1:8080/cookie")
                .cookies(Parameter.of("test", "value")).send().toTextResponse();
        boolean flag = false;
        for (Cookie cookie : response.getCookies()) {
            if (cookie.getName().equals("test")) {
                flag = true;
                break;
            }
        }
        assertTrue(flag);
    }

    @Test
    public void testBasicAuth() {
        Response<String> response = Requests.get("http://127.0.0.1:8080/basicAuth")
                .basicAuth("test", "password")
                .send().toTextResponse();
        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void testRedirect() {
        Response<String> resp = Requests.get("http://127.0.0.1:8080/redirect").userAgent("my-user-agent")
                .send().toTextResponse();
        assertEquals(200, resp.getStatusCode());
        assertTrue(resp.getBody().contains("/redirected"));
        assertTrue(resp.getBody().contains("my-user-agent"));
    }

    @Test
    public void testMultiPart() {
        String body = Requests.post("http://127.0.0.1:8080/multi_part")
                .multiPartBody(Part.file("writeTo", "keystore", this.getClass().getResourceAsStream("/keystore"))
                        .contentType("application/octem-stream"))
                .send().readToText();
        assertTrue(body.contains("writeTo"));
        assertTrue(body.contains("application/octem-stream"));
    }


    @Test
    public void testMultiPartText() {
        String body = Requests.post("http://127.0.0.1:8080/multi_part")
                .multiPartBody(Part.text("test", "this is test value"))
                .send().readToText();
        assertTrue(body.contains("this is test value"));
        assertTrue(body.contains("plain/text; charset=utf-8"));
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
                .send().readToJson(new TypeInfer<List<Integer>>() {
                });
        assertEquals(3, list.size());
    }

    @Test
    public void sendHeaders() {
        String text = Requests.get("http://127.0.0.1:8080/echo_header")
                .headers(Parameter.of("Host", "www.test.com"), Parameter.of("TestHeader", 1))
                .send().readToText();
        assertTrue(text.contains("Host: www.test.com"));
        assertTrue(text.contains("TestHeader: 1"));
    }

    @Test
    public void testHttps() {
        Response<String> response = Requests.get("https://127.0.0.1:8443/https")
                .verify(false).send().toTextResponse();
        assertEquals(200, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    public void testInterceptor() {
        final long[] elapsed = {0};
        Interceptor interceptor = new Interceptor() {
            @Override
            public RawResponse intercept(InvocationTarget target, Request request) {
                long start = System.nanoTime();
                RawResponse response = target.proceed(request);
                elapsed[0] = System.nanoTime() - start;
                return response;
            }
        };

        String text = Requests.get("http://127.0.0.1:8080/echo_header")
                .interceptors(interceptor)
                .send().readToText();
        assertTrue(elapsed[0] > 0);
    }
}