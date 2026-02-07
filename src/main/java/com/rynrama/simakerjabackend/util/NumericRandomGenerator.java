package com.rynrama.simakerjabackend.util;

import java.security.SecureRandom;

public class NumericRandomGenerator {

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final int MAX_LENGTH = 20;

    public String generate(int length) {
        if (length <= 0 || length > MAX_LENGTH) {
            throw new IllegalArgumentException("Length must be between 1 and 20");
        }

        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int digit = secureRandom.nextInt(10); // 0-9
            sb.append(digit);
        }

        return sb.toString();
    }
}
