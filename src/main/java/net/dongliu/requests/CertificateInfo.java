package net.dongliu.requests;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * For saving certificate infos
 *
 * @author Liu Dong
 */
public class CertificateInfo {
    private final String path;
    private final @Nullable String password;

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

    public @Nullable String getPassword() {
        return password;
    }
}
