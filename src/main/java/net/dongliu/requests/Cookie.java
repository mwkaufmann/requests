package net.dongliu.requests;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.io.Serializable;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;

@Immutable
public class Cookie implements Serializable {
    /**
     * <p>
     * If domain not start with ".",  means it is explicitly set and visible to it's sub-domains.
     * </p>
     * <p>
     * If the Set-Cookie header field does not have a Domain attribute, the effective domain is the domain of the request.
     * </p>
     */
    private final String domain;
    private final String path;
    private final String name;
    private final String value;
    @Nullable
    private final Instant expiry;
    private final boolean secure;

    public Cookie(String domain, String path, String name, String value, @Nullable Instant expiry, boolean secure) {
        this.domain = Objects.requireNonNull(domain);
        this.path = Objects.requireNonNull(path);
        this.name = Objects.requireNonNull(name);
        this.value = Objects.requireNonNull(value);
        this.expiry = expiry;
        this.secure = secure;
    }

    public String getDomain() {
        return domain;
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

    public boolean isSecure() {
        return secure;
    }

    @Nullable
    public Instant getExpiry() {
        return expiry;
    }

    public boolean expired(Instant now) {
        return expiry != null && expiry.isBefore(now);
    }

    public boolean match(String protocol, String host, String path) {
        if (secure && !protocol.equalsIgnoreCase("https")) {
            return false;
        }
        if (domain.startsWith(".")) {
            if (!CookieUtils.isSubDomain(domain, host)) {
                return false;
            }
        } else {
            if (!host.equals(domain)) {
                return false;
            }
        }

        if (!path.startsWith(this.path)) {
            return false;
        }
        return true;
    }

    @Override
    public boolean equals(@Nullable Object o) {
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
