package com.example.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Util {
    private static final char[] HEX_DIGITS = {
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };

    public static String getMD5String(String value) {
        return getMD5String(value.getBytes(StandardCharsets.UTF_8));
    }

    public static boolean checkPassword(String password, String md5PwdStr) {
        return getMD5String(password).equals(md5PwdStr);
    }

    public static String getMD5String(byte[] bytes) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            return bufferToHex(messageDigest.digest(bytes));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("MD5 algorithm is not available", e);
        }
    }

    private static String bufferToHex(byte[] bytes) {
        StringBuilder builder = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            appendHexPair(b, builder);
        }
        return builder.toString();
    }

    private static void appendHexPair(byte value, StringBuilder builder) {
        builder.append(HEX_DIGITS[(value & 0xf0) >> 4]);
        builder.append(HEX_DIGITS[value & 0x0f]);
    }
}
