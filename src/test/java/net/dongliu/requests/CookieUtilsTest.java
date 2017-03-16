package net.dongliu.requests;

import net.dongliu.requests.utils.CookieUtils;
import org.junit.Test;

import static org.junit.Assert.*;

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

    @Test
    public void effectivePath() {
        assertEquals("/test/", CookieUtils.effectivePath("/test/123"));
        assertEquals("/", CookieUtils.effectivePath("/"));
        assertEquals("/", CookieUtils.effectivePath(""));
    }

    @Test
    public void parseCookieHeader() throws Exception {
        String cookieValue = "__bsi=11937048251853133038_00_0_I_R_181_0303_C02F_N_I_I_0; expires=Thu, 16-Mar-17 " +
                "03:39:29 GMT; domain=www.baidu.com; path=/";
        Cookie cookie = CookieUtils.parseCookieHeader("www.baidu.com", "/", cookieValue);
        assertEquals(".www.baidu.com", cookie.getDomain());
        assertEquals(1489635569000L, cookie.getExpiry());
        assertEquals("11937048251853133038_00_0_I_R_181_0303_C02F_N_I_I_0", cookie.getValue());
        assertEquals("/", cookie.getPath());
        assertEquals("__bsi", cookie.getName());


        cookieValue = "V2EX_TAB=\"2|1:0|10:1489639030|8:V2EX_TAB|4:YWxs" +
                "|94149dfee574a182c7a43cbcb752230e9e09ca44173293ca6ab446e9e1754598\"; expires=Thu, 30 Mar 2017 " +
                "04:37:10 GMT; httponly; Path=/";
        cookie = CookieUtils.parseCookieHeader("www.v2ex.com", "/", cookieValue);
        assertEquals("www.v2ex.com", cookie.getDomain());
        assertEquals(1490848630000L, cookie.getExpiry());
        assertEquals("/", cookie.getPath());
        assertEquals("V2EX_TAB", cookie.getName());

        cookieValue = "YF-V5-G0=a2489c19ecf98bbe86a7bf6f0edcb071;Path=/";
        cookie = CookieUtils.parseCookieHeader("weibo.com", "/", cookieValue);
        assertEquals("weibo.com", cookie.getDomain());
        assertEquals(0, cookie.getExpiry());
        
        cookieValue = "ALF=1521175171; expires=Friday, 16-Mar-2018 04:39:31 GMT; path=/; domain=.sina.com.cn";
        cookie = CookieUtils.parseCookieHeader("login.sina.com.cn", "/sso/login.ph", cookieValue);
        assertEquals(".sina.com.cn", cookie.getDomain());
        assertEquals(1521175171000L, cookie.getExpiry());
        assertEquals("/", cookie.getPath());
        

    }
}