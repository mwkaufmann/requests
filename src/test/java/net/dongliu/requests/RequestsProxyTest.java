package net.dongliu.requests;

import net.dongliu.requests.mock.MockServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Liu Dong
 */
@Ignore
public class RequestsProxyTest {

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
    public void testHttpProxy() throws Exception {
        // http proxy with redirect
        RawResponse response = Requests
//                .get("http://127.0.0.1:8080")
                .get("https://www.google.com")
                .proxy(Proxies.httpProxy("127.0.0.1", 1081))
                .send();
        response.close();
        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void testSocksProxy() throws Exception {
        // socks proxy with redirect
        RawResponse response = Requests
//                .get("http://127.0.0.1:8080")
                .get("https://www.google.com")
                .proxy(Proxies.socksProxy("127.0.0.1", 1080))
                .send();
        response.close();
        assertEquals(200, response.getStatusCode());
    }
}
