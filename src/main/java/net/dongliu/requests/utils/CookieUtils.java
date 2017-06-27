package net.dongliu.requests.utils;

import net.dongliu.requests.Cookie;
import net.dongliu.requests.Parameter;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.Map;

/**
 * Http response set cookie header.
 * <p>
 * RFC 6265:
 * The origin domain of a cookie is the domain of the originating request.
 * If the origin domain is an IP, the cookie's domain attribute must not be set.
 * If a cookie's domain attribute is not set, the cookie is only applicable to its origin domain.
 * If a cookie's domain attribute is set,
 * -- the cookie is applicable to that domain and all its subdomains;
 * -- the cookie's domain must be the same as, or a parent of, the origin domain
 * -- the cookie's domain must not be a TLD, a public suffix, or a parent of a public suffix.
 */
public class CookieUtils {

    private static final char[] hexChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
            'E', 'F'};

    /**
     * Get effective path from url path
     */
    public static String effectivePath(String path) {
        int idx = path.lastIndexOf('/');
        if (idx >= 0) {
            return path.substring(0, idx + 1);
        } else {
            return "/";
        }
    }

    /**
     * Escape cookie value
     */
    public static String escape(String value) {
        int count = 0;
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c == ' ' || c == ';' || c == ',') {
                count++;
            }
        }
        if (count == 0) {
            return value;
        }
        StringBuilder sb = new StringBuilder(value.length() + count * 2);
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c == ' ' || c == ';' || c == ',') {
                sb.append('%').append(hexChars[c >> 4]).append(hexChars[c & 0xF]);
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }


    /**
     * If subDomain is sub of domain
     *
     * @param domain    start with "."
     * @param subDomain not start with "."
     * @return
     */
    public static boolean isSubDomain(String domain, String subDomain) {
        if (domain.length() - 1 == subDomain.length()) {
            return domain.endsWith(subDomain);
        } else {
            return domain.length() < subDomain.length() && subDomain.endsWith(domain);
        }
    }


    public static Cookie parseCookieHeader(String originDomain, String originPath,
                                           String headerValue) {
        String[] items = headerValue.split(";");
        Map.Entry<String, String> nameValue = parseCookieNameValue(items[0]);

        String domain = null;
        String path = null;
        long expiry = 0;
        boolean secure = false;
        for (String item : items) {
            item = item.trim();
            if (item.isEmpty()) {
                continue;
            }
            Map.Entry<String, String> attribute = parseCookieAttribute(item);
            switch (attribute.getKey().toLowerCase()) {
                case "domain":
                    domain = parseDomain(originDomain, attribute);
                    break;
                case "path":
                    path = attribute.getValue().endsWith("/") ? attribute.getValue() : attribute.getValue() + "/";
                    break;
                case "expires":
                    Date date = CookieDateUtil.parseDate(attribute.getValue());
                    if (date != null) {
                        expiry = date.getTime();
                    }
                    break;
                case "max-age":
                    try {
                        int seconds = Integer.parseInt(attribute.getValue());
                        if (seconds >= 0) {
                            expiry = System.currentTimeMillis() + seconds * 1000;
                        }
                    } catch (NumberFormatException ignore) {
                        //TODO: we should ignore this cookie?
                    }
                    break;
                case "secure":
                    secure = true;
                    break;
                case "httponly":
                    // ignore http only
                    break;
                default:
            }
        }

        return new Cookie(domain == null ? originDomain : domain, path == null ? originPath : path,
                nameValue.getKey(), nameValue.getValue(), expiry, secure);
    }

    private static Map.Entry<String, String> parseCookieNameValue(String str) {
        // Browsers always split the name and value on the first = symbol in the string
        int idx = str.indexOf("=");
        if (idx < 0) {
            // If there is no = symbol in the string at all, browsers treat it as the cookie with the empty-string name
            return Parameter.of("", str);
        } else {
            return Parameter.of(str.substring(0, idx), str.substring(idx + 1));
        }
    }

    private static Map.Entry<String, String> parseCookieAttribute(String str) {
        int idx = str.indexOf("=");
        if (idx < 0) {
            return Parameter.of(str, "");
        } else {
            return Parameter.of(str.substring(0, idx), str.substring(idx + 1));
        }
    }

    /**
     * Get domain
     * <p>
     * In RFC 2109, a domain without a leading dot meant that it could not be used on subdomains,
     * and only a leading dot (.mydomain.com) would allow it to be used across subdomains.
     * However, modern browsers respect the newer specification RFC 6265, and will ignore any leading dot,
     * meaning you can use the cookie on subdomains as well as the top-level domain.
     * </p>
     * <p>
     * We still use "." prefix to identity a explicitly set domain, if a domain without "." prefix is set, append one
     * </p>
     *
     * @return the final domain
     */
    @Nullable
    private static String parseDomain(String currentDomain, Map.Entry<String, String> pair) {
        String domain;
        domain = pair.getValue();
        if (!domain.startsWith(".")) {
            domain = "." + domain;
        }
        // ignore illegal domain value
        if (!isSubDomain(domain, currentDomain)) {
            domain = null;
        }
        return domain;
    }
}
