package net.dongliu.requests.body;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class RequestBodyTest {
    @Test
    public void json() throws Exception {
        RequestBody<List<String>> body = RequestBody.json(Arrays.asList("1", "2", "3"));
        assertEquals("application/json", body.getContentType());

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        body.writeBody(bos, StandardCharsets.UTF_8);
        String str = bos.toString("UTF-8");
        assertEquals("[\"1\",\"2\",\"3\"]", str);
    }

}