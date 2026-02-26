package com.rynrama.simakerjabackend.util;

public class NumberToIndonesian {

    private static final String[] WORDS = {
            "",
            "satu",
            "dua",
            "tiga",
            "empat",
            "lima",
            "enam",
            "tujuh"
    };

    public static String toWord(Integer number) {
        if (number == null || number < 1 || number >= WORDS.length) {
            throw new IllegalArgumentException("Number must be between 1 and 7");
        }
        return WORDS[number];
    }
}
