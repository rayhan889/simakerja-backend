package com.rynrama.simakerjabackend.util;

import com.rynrama.simakerjabackend.dto.StudentInfo;

import java.util.List;

public class FormatNumber {

    private static final String[] WORDS = {
            "",
            "satu",
            "dua",
            "tiga",
            "empat",
            "lima",
            "enam",
            "tujuh",
            "delapan",
            "sembilan",
            "sepuluh",
            "sebelas"
    };

    //    for student snapshots: [dono, joko] > 1. Dono 2. Joko
    public static String toNumberedHtmlList(List<StudentInfo> items) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            result.append(i + 1).append(". ").append(items.get(i).getFullName());
            if (i < items.size() - 1) result.append("<br/>");
        }
        return result.toString();
    }

    public static String toIndonesianWord(Integer number) {
        if (number == null || number < 1 || number >= WORDS.length) {
            throw new IllegalArgumentException("Number must be between 1 and 7");
        }
        return WORDS[number];
    }

//    2026 -> dua ribu dua puluh enam
    public static String toLongIndonesianWord(int number) {
        if (number < 12) {
            return WORDS[number];
        }
        if (number < 20) {
            return toLongIndonesianWord(number - 10) + " belas";
        }
        if (number < 100) {
            return toLongIndonesianWord(number / 10) + " puluh " + toLongIndonesianWord(number % 10);
        }
        if (number < 200) {
            return "seratus " + toLongIndonesianWord(number - 100);
        }
        if (number < 1000) {
            return toLongIndonesianWord(number / 100) + " ratus " + toLongIndonesianWord(number % 100);
        }
        if (number < 2000) {
            return "seribu " + toLongIndonesianWord(number - 1000);
        }
        if (number < 1_000_000) {
            return toLongIndonesianWord(number / 1000) + " ribu " + toLongIndonesianWord(number % 1000);
        }
        if (number < 1_000_000_000) {
            return toLongIndonesianWord(number / 1_000_000) + " juta " + toLongIndonesianWord(number % 1_000_000);
        }
        return (toLongIndonesianWord(number / 10) + " puluh " + toLongIndonesianWord(number % 10)).trim();
    }
}
