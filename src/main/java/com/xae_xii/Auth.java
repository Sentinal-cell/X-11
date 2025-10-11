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

    public int otp() {
        byte[] keyBytes = Base64.getDecoder().decode(ss);
        Key key = new SecretKeySpec(keyBytes, "HmacSHA1");

        TimeBasedOneTimePasswordGenerator totp =
                new TimeBasedOneTimePasswordGenerator();

        try {
            code = totp.generateOneTimePassword(key, Instant.now());
        } catch (Exception e) {
            logger.error("Error generating TOTP: " + e.getMessage());
        }
        logger.info("Current TOTP code: " + code);
        return code;
    }

    public boolean verifyCode(int userCode) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(ss);
            Key key = new SecretKeySpec(keyBytes, "HmacSHA1");

            TimeBasedOneTimePasswordGenerator totp =
                    new TimeBasedOneTimePasswordGenerator();

            Instant now = Instant.now();

            // Check current, previous, and next 30-second time steps
            for (int i = -1; i <= 1; i++) {
                Instant step = now.plusSeconds(i * 30L);
                int validCode = totp.generateOneTimePassword(key, step);
                if (validCode == userCode) {
                    logger.info("Valid TOTP code (offset " + i + ")");
                    return true;
                }
            }

            logger.warn("❌ Invalid TOTP code: " + userCode);
            return false;

        } catch (Exception e) {
            logger.error("Error verifying TOTP code: " + e.getMessage());
            return false;
        }
    }
}