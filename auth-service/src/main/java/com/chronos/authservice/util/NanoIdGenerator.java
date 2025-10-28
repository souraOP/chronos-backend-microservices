package com.chronos.authservice.util;

import java.security.SecureRandom;

// took it from the NanoId Generator class
public class NanoIdGenerator {
    public static final SecureRandom DEFAULT_NUMBER_GENERATOR = new SecureRandom();
    public static final char[] DEFAULT_ALPHABET = "_-0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
}
