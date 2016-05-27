package net.dongliu.requests;

import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * @author Liu Dong
 */
@Ignore
public class RequestsTest2 {

    //TODO: url connection https verify
    @Test
    public void testClientSetting() throws IOException {
        RawResponse response = Requests
                .get("https://kyfw.12306.cn/otn/")
                //.get("https://127.0.0.1:8443/https")
                .timeout(3_000)
                .compress(false)
                .followRedirect(false)
                .userAgent("Custom user agent")
                .verify(false).send();
        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void testHttps() throws IOException {
        RawResponse response = Requests
//                .get("https://127.0.0.1:8443/https")
                .get("https://kyfw.12306.cn/otn/")
                .verify(false).send();
        String s = response.readToText();
        assertEquals(200, response.getStatusCode());
        assertFalse(s.isEmpty());
    }


    @Test
    public void testHttpProxy() throws Exception {
        // http proxy with redirect
        RawResponse response = Requests
//                .get("http://127.0.0.1:8080")
                .get("https://www.google.com")
                .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 1081)))
                .send();
        response.close();
        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void testSocksProxy() throws Exception {
        // socks proxy with redirect
        RawResponse response = Requests
//                .get("http://127.0.0.1:8080")
                .get("https://www.google.com")
                .proxy(new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("127.0.0.1", 1080)))
                .send();
        response.close();
        assertEquals(200, response.getStatusCode());
    }
}
