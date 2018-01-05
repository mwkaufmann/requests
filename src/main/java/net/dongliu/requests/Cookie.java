package net.dongliu.requests;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

@Immutable
public class Cookie implements Map.Entry<String, String>, Serializable {
    private static final long serialVersionUID = -287880603936079757L;
    /**
     * The cookie domain set by attribute or from url
     */
    @Nonnull
    private final String domain;
    /**
     * The cookie path set by attribute or from url
     */
    @Nonnull
    private final String path;
    @Nonnull
    private final String name;
    @Nonnull
    private final String value;
    /**
     * The cookie expire timestamp, zero means no expiry is set
     */
    private final long expiry;
    /**
     * If secure attribute is set
     */
    private final boolean secure;

    /**
     * If true, the cookie did not set domain attribute
     */
    private final boolean hostOnly;

    public Cookie(String domain, String path, String name, String value, long expiry, boolean secure,
                  boolean hostOnly) {
        this.domain = Objects.requireNonNull(domain);
        this.path = Objects.requireNonNull(path);
        this.name = Objects.requireNonNull(name);
        this.value = Objects.requireNonNull(value);
        this.expiry = expiry;
        this.secure = secure;
        this.hostOnly = hostOnly;
    }

    @Nonnull
    public String getDomain() {
        return domain;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    @Override
    public String getKey() {
        return name;
    }

    @Nonnull
    public String getValue() {
        return value;
    }

    @Override
    public String setValue(String value) {
        throw new UnsupportedOperationException();
    }

    public boolean isSecure() {
        return secure;
    }

    public long getExpiry() {
        return expiry;
    }

    /**
     * If cookie is expired
     */
    public boolean expired(long now) {
        return expiry != 0 && expiry < now;
    }

    @Nonnull
    public String getPath() {
        return path;
    }

    public boolean isHostOnly() {
        return hostOnly;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Cookie cookie = (Cookie) o;

        if (expiry != cookie.expiry) return false;
        if (secure != cookie.secure) return false;
        if (hostOnly != cookie.hostOnly) return false;
        if (!domain.equals(cookie.domain)) return false;
        if (!path.equals(cookie.path)) return false;
        if (!name.equals(cookie.name)) return false;
        return value.equals(cookie.value);
    }

    @Override
    public int hashCode() {
        int result = domain.hashCode();
        result = 31 * result + path.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + value.hashCode();
        result = 31 * result + (int) (expiry ^ (expiry >>> 32));
        result = 31 * result + (secure ? 1 : 0);
        result = 31 * result + (hostOnly ? 1 : 0);
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
                ", secure=" + secure +
                ", hostOnly=" + hostOnly +
                '}';
    }
}
