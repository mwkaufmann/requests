package net.dongliu.requests;

import net.dongliu.requests.exception.RequestsException;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Map;

class Utils {

    private static DateTimeFormatter rfc1123Formatter = DateTimeFormatter.RFC_1123_DATE_TIME;

    public static Instant parseDate(String dateStr) {
        return rfc1123Formatter.parse(dateStr, Instant::from);
    }

    public static String formatDate(Instant instant) {
        return rfc1123Formatter.format(instant);
    }

    public static URL joinUrl(String url, Collection<Map.Entry<String, String>> params, Charset charset) {
        String fullUrl;
        if (params.isEmpty()) {
            fullUrl = url;
        } else {
            fullUrl = url + "?" + URIEncoder.encodeQueries(params, charset);
        }
        try {
            return new URL(fullUrl);
        } catch (MalformedURLException e) {
            throw new RequestsException(e);
        }
    }

    public static boolean isRedirect(int status) {
        return status == 300 || status == 301 || status == 302 || status == 303 || status == 307 || status == 308;
    }
}
