package com.rynrama.simakerjabackend.util;

public class EnumUtil {

    public static <E extends Enum<E>> boolean containsString(Class<E> enumClass, String name) {
        if (name == null) {
            return false;
        }
        try {
            // valueOf() throws an IllegalArgumentException if the name is invalid
            Enum.valueOf(enumClass, name);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
