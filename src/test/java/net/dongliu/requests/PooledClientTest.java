package net.dongliu.requests;

import net.dongliu.requests.mock.MockServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class PooledClientTest {

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
        try(PooledClient pooledClient = PooledClient.custom().build()) {
            for (int i = 0; i < 100; i++) {
                Response<String> response = pooledClient.get("http://127.0.0.1:8080/").text();
                assertEquals(200, response.getStatusCode());
            }
        }
    }

    @Test
    public void testPooledHttps() throws IOException {
        try (PooledClient pooledClient = PooledClient.custom().verify(false).build()) {
            Response<String> response = pooledClient.get("https://127.0.0.1:8443/otn/").text();
            assertEquals(200, response.getStatusCode());
        }
    }

    @Test
    public void testSession() throws Exception {
        try (PooledClient pooledClient = PooledClient.custom().verify(false).build()) {
            Session session = pooledClient.session();
            Response<String> response = session.get("https://127.0.0.1:8443/otn/").text();
            assertEquals(200, response.getStatusCode());
        }
    }
}