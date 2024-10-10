package com.mmieczkowski.serverguard.service;

import java.security.SecureRandom;
import java.util.Random;

public class SecureRandomString {
    private static final Random random = new SecureRandom();

    private static final String charPool = "ABCDEFGJKLMNPRSTUVWXYZ0123456789";

    public static String generate(int length) {
        return random
                .ints(length, 0, charPool.length()) // 9 is the length of the string you want
                .mapToObj(charPool::charAt)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }
}
