package net.dongliu.requests;

import net.dongliu.requests.mock.MockServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SessionTest {
    @Test
    public void updateCookie() throws Exception {
        Session session = new Session();
        Set<Cookie> set1 = setOf(
                new Cookie("test.com", "/", "test", "value", 0, false),
                new Cookie("test.com", "/test/", "test1", "value1", 0, false),
                new Cookie("test1.com", "/", "test2", "value2", 0, false)
        );
        session.updateCookie(set1);
        assertEquals(set1, session.getCookies());

        Set<Cookie> set2 = setOf(
                new Cookie("test.com", "/test/", "test1", "value1",
                        System.currentTimeMillis() - TimeUnit.HOURS.toMillis(1), false),
                new Cookie("test1.com", "/", "test2", "value2", System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1),
                        false)
        );
        session.updateCookie(set2);
        assertEquals(setOf(
                new Cookie("test.com", "/", "test", "value", 0, false),
                new Cookie("test1.com", "/", "test2", "value2",
                        System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1), false)
        ), session.getCookies());
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

    private <T> Set<T> setOf(T... values) {
        Set<T> set = new HashSet<>();
        for (T v : values) {
            set.add(v);
        }
        return set;
    }
}