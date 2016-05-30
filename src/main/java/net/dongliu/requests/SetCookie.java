package net.dongliu.requests;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Http response set cookie header
 */
class SetCookie {

    private List<Map.Entry<String, String>> cookies = new ArrayList<>();
    private String domain;
    private boolean bareDomain;
    private String path;
    private Optional<Instant> expiry = Optional.empty();

    public List<Map.Entry<String, String>> getCookies() {
        return cookies;
    }

    public void addCookie(Map.Entry<String, String> cookie) {
        this.cookies.add(cookie);
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public boolean isBareDomain() {
        return bareDomain;
    }

    public void setBareDomain(boolean bareDomain) {
        this.bareDomain = bareDomain;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Optional<Instant> getExpiry() {
        return expiry;
    }

    public void setExpiry(Optional<Instant> expiry) {
        this.expiry = expiry;
    }

    public void setExpiry(Instant expiry) {
        this.expiry = Optional.of(expiry);
    }

    public static SetCookie parse(String value) {
        SetCookie setCookie = new SetCookie();
        for (String item : value.split("; ")) {
            Map.Entry<String, String> pair = Utils.pairFrom(item);
            switch (pair.getKey().toLowerCase()) {
                case "domain":
                    String domain = pair.getValue();
                    if (domain.startsWith(".")) {
                        domain = domain.substring(1);
                    }
                    setCookie.setDomain(domain);
                    setCookie.setBareDomain(false);
                    break;
                case "path":
                    setCookie.setPath(pair.getValue().endsWith("/") ? pair.getValue() : pair.getValue() + "/");
                    break;
                case "expires":
                    try {
                        Instant instant = Utils.parseDate(pair.getValue());
                        setCookie.setExpiry(instant);
                    } catch (DateTimeParseException ignore) {
                    }
                    break;
                case "max-age":
                    try {
                        int seconds = Integer.parseInt(pair.getValue());
                        if (seconds >= 0) {
                            Instant instant = Instant.now().plusSeconds(seconds);
                            setCookie.setExpiry(instant);
                        }
                    } catch (NumberFormatException ignore) {
                    }
                    break;
                case "":
                    if (value.equalsIgnoreCase("HttpOnly") || value.equalsIgnoreCase("Secure")) {
                        break;
                    }
                default:
                    setCookie.addCookie(pair);
            }
        }
        return setCookie;
    }
}
