package net.dongliu.requests;

import java.io.Serializable;
import java.time.Instant;
import java.util.Optional;

public class Cookie implements Serializable {
    private final String domain;
    private final boolean bareDomain;
    private final String path;
    private final String name;
    private final String value;
    private final Optional<Instant> expiry;

    public Cookie(String domain, boolean bareDomain, String path, String name, String value, Optional<Instant> expiry) {
        this.domain = domain;
        this.bareDomain = bareDomain;
        this.path = path;
        this.name = name;
        this.value = value;
        this.expiry = expiry;
    }

    public String getDomain() {
        return domain;
    }

    public boolean isBareDomain() {
        return bareDomain;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public Optional<Instant> getExpiry() {
        return expiry;
    }

    public boolean expired(Instant now) {
        return expiry.isPresent() && expiry.get().isBefore(now);
    }

    public boolean match(String host, String path) {
        if (!domain.equals(host)) {
            if (bareDomain || !host.endsWith("." + domain)) {
                return false;
            }
        }
        if (!path.startsWith(this.path)) {
            return false;
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Cookie cookie = (Cookie) o;

        if (!domain.equals(cookie.domain)) return false;
        if (!path.equals(cookie.path)) return false;
        return name.equals(cookie.name);

    }

    @Override
    public int hashCode() {
        int result = domain.hashCode();
        result = 31 * result + path.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Cookie{" +
                "domain='" + domain + '\'' +
                ", path='" + path + '\'' +
                ", name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", expiry=" + expiry +
                '}';
    }
}
