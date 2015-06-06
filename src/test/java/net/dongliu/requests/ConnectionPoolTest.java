package net.dongliu.requests;

import net.dongliu.requests.mock.MockServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class ConnectionPoolTest {

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
        try(ConnectionPool connectionPool = ConnectionPool.custom().build()) {
            for (int i = 0; i < 100; i++) {
                Response<String> response = connectionPool.get("http://127.0.0.1:8080/").text();
                assertEquals(200, response.getStatusCode());
            }
        }
    }

    @Test
    public void testPooledHttps() throws IOException {
        try (ConnectionPool connectionPool = ConnectionPool.custom().verify(false).build()) {
            Response<String> response = connectionPool.get("https://127.0.0.1:8443/otn/").text();
            assertEquals(200, response.getStatusCode());
        }
    }

    @Test
    public void testSession() throws Exception {
        try (ConnectionPool connectionPool = ConnectionPool.custom().verify(false).build()) {
            Session session = connectionPool.session();
            Response<String> response = session.get("https://127.0.0.1:8443/otn/").text();
            assertEquals(200, response.getStatusCode());
        }
    }
}