package net.dongliu.requests;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Objects;

/**
 * For saving certificate infos
 *
 * @author Liu Dong
 */
public class CertificateInfo implements Serializable {
    private static final long serialVersionUID = -3880008161985636331L;
    private final String path;
    private final @Nullable
    String password;

    /**
     * @param path should not be null
     */
    public CertificateInfo(String path) {
        this.path = Objects.requireNonNull(path);
        this.password = null;
    }

    /**
     * @param path     should not be null
     * @param password can not be null
     */
    public CertificateInfo(String path, @Nullable String password) {
        this.path = Objects.requireNonNull(path);
        this.password = password;
    }

    public String getPath() {
        return path;
    }

    public @Nullable
    String getPassword() {
        return password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CertificateInfo that = (CertificateInfo) o;

        if (path != null ? !path.equals(that.path) : that.path != null) return false;
        return password != null ? password.equals(that.password) : that.password == null;

    }

    @Override
    public int hashCode() {
        int result = path != null ? path.hashCode() : 0;
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }
}
