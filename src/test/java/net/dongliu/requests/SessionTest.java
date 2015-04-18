package net.dongliu.requests;

import net.dongliu.requests.mock.MockServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class SessionTest {

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
    public void testSession() {
        Session session = Requests.session();
        Response<String> response = session.get("http://127.0.0.1:8080").text();
        assertEquals(200, response.getStatusCode());
    }
}