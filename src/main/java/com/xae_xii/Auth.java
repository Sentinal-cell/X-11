package com.xae_xii;
import javax.crypto.spec.SecretKeySpec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;

import java.security.Key;
import java.time.Instant;
import java.util.Base64;
public class Auth {
    private static final String ss = "KRl7v3GjWvLz7tvPn9tOpnukvKQ=";
    private static final Logger logger = LogManager.getLogger(Auth.class);
    public static int code;
    public int otp(){
        // Convert Base64 string back into a key
        byte[] keyBytes = Base64.getDecoder().decode(ss);
        Key key = new SecretKeySpec(keyBytes, "HmacSHA1");

        // Create TOTP generator (default: 30s step, 6 digits)
        TimeBasedOneTimePasswordGenerator totp =
                new TimeBasedOneTimePasswordGenerator();

        // Generate expected code for right now
        try {
            code = totp.generateOneTimePassword(key, Instant.now());
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("Current TOTP code: " + code);
        return code;
    }
}
