package com.rynrama.simakerjabackend.util;

public final class IndonesianNumberConverter {

    private static final String[] BASIC = {
            "", "satu", "dua", "tiga", "empat",
            "lima", "enam", "tujuh", "delapan", "sembilan",
            "sepuluh", "sebelas"
    };

    public static String toWords(int number) {
        if (number < 12) {
            return BASIC[number];
        }
        if (number < 20) {
            return toWords(number - 10) + " belas";
        }
        if (number < 100) {
            return toWords(number / 10) + " puluh " + toWords(number % 10);
        }
        if (number < 200) {
            return "seratus " + toWords(number - 100);
        }
        if (number < 1000) {
            return toWords(number / 100) + " ratus " + toWords(number % 100);
        }
        if (number < 2000) {
            return "seribu " + toWords(number - 1000);
        }
        if (number < 1_000_000) {
            return toWords(number / 1000) + " ribu " + toWords(number % 1000);
        }
        if (number < 1_000_000_000) {
            return toWords(number / 1_000_000) + " juta " + toWords(number % 1_000_000);
        }
        return (toWords(number / 10) + " puluh " + toWords(number % 10)).trim();
    }
}

