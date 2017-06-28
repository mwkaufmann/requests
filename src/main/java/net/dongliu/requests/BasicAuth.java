package net.dongliu.requests;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

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
        this.user = Objects.requireNonNull(user);
        this.password = Objects.requireNonNull(password);
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
        return "Basic " + base64Encode((user + ":" + password).getBytes(UTF_8));
    }

    // Java7 do not have open base64 API. If do not support Java7 in future, remove this base64 impl.
    private static byte[] table = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
            'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',
            'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
            'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'};

    private static String base64Encode(byte[] data) {

        int dstLen = 4 * ((data.length + 2) / 3);
        byte[] dst = new byte[dstLen];

        int num = data.length / 3;
        for (int i = 0; i < num; i++) {
            dst[i * 4] = table[data[i * 3] >> 2];
            dst[i * 4 + 1] = table[(data[i * 3] & 0x3) << 4 | data[i * 3 + 1] >> 4];
            dst[i * 4 + 2] = table[(data[i * 3 + 1] & 0xf) << 2 | data[i * 3 + 2] >> 6];
            dst[i * 4 + 3] = table[data[i * 3 + 2] & 0x3f];
        }

        int remain = data.length - num * 3;
        if (remain == 1) {
            dst[num * 4] = table[data[num * 3] >> 2];
            dst[num * 4 + 1] = table[(data[num * 3] & 0x3) << 4];
            dst[num * 4 + 2] = '=';
            dst[num * 4 + 3] = '=';
        } else if (remain == 2) {
            dst[num * 4] = table[data[num * 3] >> 2];
            dst[num * 4 + 1] = table[(data[num * 3] & 0x3) << 4 | data[num * 3 + 1] >> 4];
            dst[num * 4 + 2] = table[(data[num * 3 + 1] & 0xf) << 2];
            dst[num * 4 + 3] = '=';
        }
        return new String(dst, StandardCharsets.US_ASCII);
    }

    public static void main(String[] args) {
        System.out.println(base64Encode("123456".getBytes()));
    }
}
