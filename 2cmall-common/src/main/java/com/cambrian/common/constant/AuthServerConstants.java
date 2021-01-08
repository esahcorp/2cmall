package com.cambrian.common.constant;

/**
 * @author kuma 2021-01-03
 */
public class AuthServerConstants {

    public static final String SERVICE_CODE = "auth";

    public static final class CacheKey {
        private CacheKey() {
            throw new UnsupportedOperationException("Until class");
        }

        public static final String VERIFICATION_PREFIX = SERVICE_CODE + ":verifi:";
    }

    public static final String SESSION_KEY_LOGIN_USER = "loginUser";
}
