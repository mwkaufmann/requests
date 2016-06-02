package net.dongliu.requests;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Liu Dong
 */
public class CookieUtilsTest {
    @Test
    public void isSubDomain() throws Exception {
        assertTrue(CookieUtils.isSubDomain(".baidu.com", "www.baidu.com"));
        assertTrue(CookieUtils.isSubDomain(".baidu.com", "baidu.com"));
        assertFalse(CookieUtils.isSubDomain(".baidu.com", "a.com"));
        assertFalse(CookieUtils.isSubDomain(".baidu.com", "ww.a.com"));
    }

    @Test
    public void escape() {
        assertEquals("test%3B_%2C%20", CookieUtils.escape("test;_, "));
        assertEquals("test_==", CookieUtils.escape("test_=="));
    }
}