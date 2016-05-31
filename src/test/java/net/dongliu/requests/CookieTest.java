package net.dongliu.requests;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Liu Dong
 */
public class CookieTest {
    @Test
    public void match() throws Exception {
        Cookie cookie = new Cookie("test.com", "/", "test", "value", null, false);
        assertTrue(cookie.match("http", "test.com", "/test/"));

        // https
        cookie = new Cookie("test.com", "/", "test", "value", null, true);
        assertFalse(cookie.match("http", "test.com", "/test/"));
        assertTrue(cookie.match("https", "test.com", "/test/"));
    }

}