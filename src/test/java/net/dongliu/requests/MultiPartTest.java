package net.dongliu.requests;

import net.dongliu.requests.struct.Part;
import org.junit.Test;

import java.io.File;

public class MultiPartTest {

    @Test
    public void testOf() throws Exception {
        Part multiPart = new Part("writeTo", new File("MultiPartTest.java"));
    }
}