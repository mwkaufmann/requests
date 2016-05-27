package net.dongliu.requests;

import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Http Basic Authentication
 *
 * @author Liu Dong
 */
public class BasicAuth {
    private final String user;
    private final String password;

    public BasicAuth(String user, String password) {
        this.user = user;
        this.password = password;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    /**
     * Encode to http header
     */
    public String encode() {
        return "Basic " + Base64.getEncoder().encodeToString((user + ":" + password).getBytes(UTF_8));
    }

}
