package net.dongliu.requests;

import net.dongliu.requests.struct.MultiPart;
import org.junit.Test;

import java.io.File;

public class MultiPartTest {

    @Test
    public void testOf() throws Exception {
        MultiPart multiPart = new MultiPart("file", new File("MultiPartTest.java"));
    }
}