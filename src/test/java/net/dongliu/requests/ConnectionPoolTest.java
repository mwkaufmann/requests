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
        ConnectionPool connectionPoll = ConnectionPool.custom().build();
        for (int i = 0; i < 100; i++) {
            Response<String> response = Requests.get("http://127.0.0.1:8080/")
                    .connectionPool(connectionPoll)
                    .text();
            assertEquals(200, response.getStatusCode());
        }
        connectionPoll.close();
    }

    @Test
    public void testPooledHttps() throws IOException {
        ConnectionPool connectionPoll = ConnectionPool.custom().verify(false).build();
        Response<String> response = Requests.get("https://127.0.0.1:8443/otn/")
                .connectionPool(connectionPoll)
                .text();
        assertEquals(200, response.getStatusCode());
        connectionPoll.close();
    }
}