package net.dongliu.requests;

import org.junit.Test;

import static org.junit.Assert.*;

public class ClientBuilderTest {
    @Test
    public void create() throws Exception {
        Client client = new ClientBuilder().maxConnectionPerHost(3)
                .maxConnection(5)
                .connectTimeout(3000)
                .socksTimeout(5000)
                .userAgent("test agent")
                .verify(false).compress(false)
                .keepAlive(false)
                .followRedirect(false)
                .create();
        assertEquals(3000, client.getConnectTimeout());
        assertEquals(5000, client.getSocksTimeout());
        assertEquals(5, client.getMaxConnection());
        assertEquals(3, client.getMaxConnectionPerHost());
        assertEquals("test agent", client.getUserAgent());
        assertFalse(client.isVerify());
        assertFalse(client.isCompress());
        assertFalse(client.isKeepAlive());
        assertFalse(client.isFollowRedirect());

        client = new ClientBuilder().timeout(2000).create();
        assertEquals(2000, client.getConnectTimeout());
        assertEquals(2000, client.getSocksTimeout());
    }

}