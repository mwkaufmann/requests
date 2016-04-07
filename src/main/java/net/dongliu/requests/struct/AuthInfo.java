package net.dongliu.requests.struct;

import javax.annotation.concurrent.Immutable;

/**
 * Auth info
 *
 * @author Dong Liu dongliu@live.cn
 */
@Immutable
public class AuthInfo {
    private final String userName;
    private final String password;

    public AuthInfo(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }
}
