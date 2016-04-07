package net.dongliu.requests.struct;

import org.apache.http.annotation.Immutable;

import javax.annotation.Nullable;

/**
 * host, contains domain and port
 *
 * @author Dong Liu dongliu@live.cn
 */
@Immutable
public class Host {
    private final String domain;
    private final int port;

    /**
     * create host with domain and port
     */
    public Host(String domain, int port) {
        this.domain = domain;
        this.port = port;
    }

    /**
     * create host with domain and default port 80
     */
    public Host(String domain) {
        this.domain = domain;
        this.port = 80;
    }

    public String getDomain() {
        return domain;
    }


    public int getPort() {
        return port;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Host host = (Host) o;

        if (port != host.port) return false;
        if (!domain.equals(host.domain)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = domain.hashCode();
        result = 31 * result + port;
        return result;
    }

    @Override
    public String toString() {
        return "Host " + domain + ":" + port;
    }
}
