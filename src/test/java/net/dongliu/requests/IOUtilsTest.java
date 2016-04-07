package net.dongliu.requests;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.Assert.*;

/**
 * @author Liu Dong
 */
public class IOUtilsTest {
    @Test
    public void testToString() throws Exception {
        StringReader reader = new StringReader("This is a test");
        assertEquals("This is a test", IOUtils.toString(reader, -1));
    }

    @Test
    public void copy() throws Exception {
        StringReader reader = new StringReader("This is a test");
        StringWriter writer = new StringWriter();
        IOUtils.copy(reader, writer, -1);
        assertEquals("This is a test", writer.toString());

        ByteArrayInputStream in = new ByteArrayInputStream(new byte[]{1, 2, 3, 4, 5, 6});
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        IOUtils.copy(in, out, -1);
        assertArrayEquals(new byte[]{1, 2, 3, 4, 5, 6}, out.toByteArray());
    }
}