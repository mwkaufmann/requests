package net.dongliu.requests;

import org.junit.Test;

import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class DefaultCookieJarTest {
    @Test
    public void storeCookies() throws Exception {
        long now = System.currentTimeMillis();
        long oneHour = TimeUnit.HOURS.toMillis(1);
        CookieJar cookieJar = new DefaultCookieJar();
        List<Cookie> cookies = Arrays.asList(
                new Cookie("test.com", "/", "test", "value", 0, false, false),
                new Cookie("test.com", "/test/", "test", "value1", 0, false, false),
                new Cookie("test.com", "/test/", "test1", "value1", 0, false, false),
                new Cookie("test1.com", "/", "test2", "value2", 0, false, false)
        );
        cookieJar.storeCookies(cookies);
        assertEquals(new HashSet<>(cookies), new HashSet<>(cookieJar.getCookies()));

        List<Cookie> cookies1 = Arrays.asList(
                new Cookie("test.com", "/test/", "test1", "value1", now - oneHour, false, false),
                new Cookie("test1.com", "/", "test2", "value2", now + oneHour, false, false)
        );
        cookieJar.storeCookies(cookies1);
        assertEquals(new HashSet<>(Arrays.asList(
                new Cookie("test.com", "/", "test", "value", 0, false, false),
                new Cookie("test.com", "/test/", "test", "value1", 0, false, false),
                new Cookie("test1.com", "/", "test2", "value2", now + oneHour, false, false)
        )), new HashSet<>(cookieJar.getCookies()));
    }

    @Test
    public void getCookies() throws Exception {
        CookieJar cookieJar = new DefaultCookieJar();
        List<Cookie> cookies = Arrays.asList(
                new Cookie("test.com", "/", "test", "value", 0, false, false),
                new Cookie("test.com", "/test/", "test", "value1", 0, false, false),
                new Cookie("test.com", "/test/", "test1", "value1", 0, false, false),
                new Cookie("test1.com", "/", "test2", "value2", 0, false, false)
        );
        cookieJar.storeCookies(cookies);

        URL url = new URL("http://www.test.com/test/");
        List<Cookie> matched = cookieJar.getCookies(url);
        assertEquals(3, matched.size());
        assertEquals(new HashSet<>(Arrays.asList(
                new Cookie("test.com", "/test/", "test", "value1", 0, false, false),
                new Cookie("test.com", "/test/", "test1", "value1", 0, false, false)
        )), new HashSet<>(matched.subList(0, 2)));
        assertEquals(new Cookie("test.com", "/", "test", "value", 0, false, false), matched.get(2));
    }
}