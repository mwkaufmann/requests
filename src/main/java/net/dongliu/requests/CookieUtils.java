package net.dongliu.requests;

import javax.annotation.Nullable;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
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
class CookieUtils {

    /**
     * If subDomain is sub of domain
     *
     * @param domain    start with "."
     * @param subDomain not start with "."
     * @return
     */
    static boolean isSubDomain(String domain, String subDomain) {
        if (domain.length() - 1 == subDomain.length()) {
            return domain.endsWith(subDomain);
        } else {
            return domain.length() < subDomain.length() && subDomain.endsWith(domain);
        }
    }


    static List<Cookie> parseCookieHeader(String originDomain, String originPath,
                                          String headerValue) {
        List<Map.Entry<String, String>> nameValues = new ArrayList<>();
        String domain = null;
        String path = null;
        Instant expiry = null;
        boolean secure = false;
        for (String item : headerValue.split("; ")) {
            Map.Entry<String, String> pair = Utils.pairFrom(item);
            switch (pair.getKey().toLowerCase()) {
                case "domain":
                    domain = parseDomain(originDomain, pair);
                    break;
                case "path":
                    path = pair.getValue().endsWith("/") ? pair.getValue() : pair.getValue() + "/";
                    break;
                case "expires":
                    try {
                        expiry = Utils.parseDate(pair.getValue());
                    } catch (DateTimeParseException ignore) {
                        //TODO: we should ignore this cookie?
                    }
                    break;
                case "max-age":
                    try {
                        int seconds = Integer.parseInt(pair.getValue());
                        if (seconds >= 0) {
                            expiry = Instant.now().plusSeconds(seconds);
                        }
                    } catch (NumberFormatException ignore) {
                        //TODO: we should ignore this cookie?
                    }
                    break;
                case "":
                    if (headerValue.equalsIgnoreCase("Secure")) {
                        secure = true;
                        break;
                    }
                    // ignore http only
                    if (headerValue.equalsIgnoreCase("HttpOnly")) {
                        break;
                    }
                default:
                    nameValues.add(pair);
            }
        }

        List<Cookie> cookies = new ArrayList<>(nameValues.size());
        for (Map.Entry<String, String> nameValue : nameValues) {
            cookies.add(new Cookie(domain == null ? originDomain : domain, path == null ? originPath : path,
                    nameValue.getKey(), nameValue.getValue(), expiry, secure));
        }

        return cookies;
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
     * @return
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
