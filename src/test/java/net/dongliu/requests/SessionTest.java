package net.dongliu.requests;

import net.dongliu.requests.mock.MockServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SessionTest {
    @Test
    public void updateCookie() throws Exception {
        Session session = new Session();
        Set<Cookie> set1 = new HashSet<>(Arrays.asList(
                new Cookie("test.com", "/", "test", "value", null, false),
                new Cookie("test.com", "/test/", "test1", "value1", null, false),
                new Cookie("test1.com", "/", "test2", "value2", null, false)
        ));
        session.updateCookie(set1);
        assertEquals(set1, session.getCookies());

        Set<Cookie> set2 = new HashSet<>(Arrays.asList(
                new Cookie("test.com", "/test/", "test1", "value1", Instant.now().minus(1, ChronoUnit.HOURS), false),
                new Cookie("test1.com", "/", "test2", "value2", Instant.now().plus(1, ChronoUnit.HOURS), false)
        ));
        session.updateCookie(set2);
        assertEquals(new HashSet<>(Arrays.asList(
                new Cookie("test.com", "/", "test", "value", null, false),
                new Cookie("test1.com", "/", "test2", "value2", Instant.now().plus(1, ChronoUnit.HOURS), false)
        )), session.getCookies());
    }

    @Test
    public void matchedCookies() throws Exception {

    }

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
        String response = session.get("http://127.0.0.1:8080").send().readToText();
        assertTrue(!response.isEmpty());
    }
}