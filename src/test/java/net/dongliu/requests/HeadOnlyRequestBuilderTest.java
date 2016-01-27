package net.dongliu.requests;

import org.junit.Test;

import java.net.URI;

import static org.junit.Assert.assertEquals;

/**
 * @author Liu Dong
 */
public class HeadOnlyRequestBuilderTest {

    @Test
    public void testBuild() throws Exception {
        Request request = new HeadOnlyRequestBuilder().url("http://www.baidu.com").build();
        assertEquals(new URI("http://www.baidu.com"), request.getUrl());
    }
}