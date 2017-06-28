package net.dongliu.requests.body;

import net.dongliu.requests.utils.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;

import static net.dongliu.requests.HttpHeaders.CONTENT_TYPE_BINARY;

/**
 * @author Liu Dong
 */
class FileRequestBody extends RequestBody<File> {
    FileRequestBody(File body) {
        super(body, getFileContentType(body), false);
    }

    private static String getFileContentType(File body) {
        String contentType;
        try {
            contentType = Files.probeContentType(body.toPath());
        } catch (IOException e) {
            contentType = null;
        }
        if (contentType == null) {
            contentType = CONTENT_TYPE_BINARY;
        }
        return contentType;
    }

    @Override
    public void writeBody(OutputStream os, Charset charset) throws IOException {
        IOUtils.copy(new FileInputStream(getBody()), os);
    }
}
