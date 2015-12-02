package net.dongliu.requests;

import org.junit.Test;

import java.net.URI;
import java.net.URL;

import static org.junit.Assert.*;

/**
 * @author Liu Dong
 */
public class BaseRequestBuilderTest {

    @Test
    public void testBuild() throws Exception {
        Request request = new BaseRequestBuilder().url("http://www.baidu.com").build();
        assertEquals(new URI("http://www.baidu.com"), request.getUrl());
    }
}