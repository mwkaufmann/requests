package net.dongliu.requests;

import net.dongliu.requests.body.Part;
import org.junit.Test;

import java.io.File;

public class MultiPartTest {

    @Test
    public void testOf() throws Exception {
        Part multiPart = Part.file("writeTo", new File("MultiPartTest.java"));
    }
}