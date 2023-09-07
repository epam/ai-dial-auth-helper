package com.epam.aidial.auth.helper.utils;

import com.auth0.jwt.interfaces.Claim;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;

public class Utils {
    private Utils() {
        throw new IllegalStateException("Utility class");
    }

    public static final String BEARER = "bearer ";

    public static String getTokenFromAuth(String auth) {
        if (auth == null)
            return null;
        if (auth.regionMatches(true, 0, BEARER, 0, BEARER.length()))
            return auth.substring(BEARER.length());
        return null;
    }

    public static String decodeToJson(final String base64){
        return StringUtils.newStringUtf8(Base64.decodeBase64(base64));
    }

    public static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

    public static boolean isClaimMissing(Claim claim) {
        return claim == null || claim.isMissing() || claim.isNull();
    }
}
