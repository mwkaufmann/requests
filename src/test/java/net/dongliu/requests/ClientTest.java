package net.dongliu.requests;

import net.dongliu.requests.mock.MockServer;
import net.dongliu.requests.struct.Proxy;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

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
    public void testMultiThread() throws IOException {
        try (Client client = Client.single().build()) {
            for (int i = 0; i < 100; i++) {
                String response = client.get("http://127.0.0.1:8080/").send().readToText();
                assertFalse(response.isEmpty());
            }
        }
    }

    @Test
    public void testHttps() throws IOException {
        try (Client client = Client.single().verify(false).build()) {
            String response = client.get("https://127.0.0.1:8443/otn/").send().readToText();
            assertFalse(response.isEmpty());
        }
    }

    @Test
    public void testSession() throws Exception {
        try (Client client = Client.single().verify(false).build()) {
            Session session = client.session();
            String response = session.get("https://127.0.0.1:8443/otn/").send().readToText();
            assertFalse(response.isEmpty());
        }
    }

    @Test
    @Ignore("launch a proxy first to run this test")
    public void testHttpProxy() {
        try (Client client = Client.single().proxy(Proxy.httpProxy("127.0.0.1", 1081)).build()) {
            RawResponse resp = client.get("http://www.baidu.com/").send();
            String content = resp.readToText();
            assertEquals(200, resp.getStatus());
            assertFalse(content.isEmpty());
        }
    }

    @Test
    @Ignore("launch a proxy first to run this test")
    public void testSocksProxy() {
        try (Client client = Client.single().verify(false).proxy(Proxy.socksProxy("127.0.0.1", 1080)).build()) {
            RawResponse resp = client.get("http://stackoverflow.com/").send();
            String content = resp.readToText();
            assertEquals(200, resp.getStatus());
            assertFalse(content.isEmpty());
        }
    }
}