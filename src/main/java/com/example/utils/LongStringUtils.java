package com.example.utils;

public class LongStringUtils {

    private static final int MAX_CHARS = 60;
    private static final String STR_SUFFIX = " ...>";

    private LongStringUtils() {
        throw new UnsupportedOperationException();
    }

    public static String formatLongString(Object input) {
        if (input == null) {
            return "{null}";
        }
        String string = input.toString();
        if ("".equals(string)) {
            return "{empty}";
        }

        int length = string.length();
        if (length <= MAX_CHARS) {
            return string;
        } else {
            String substring = string.substring(0, MAX_CHARS);
            int indexOf = substring.lastIndexOf(' ');
            if (indexOf > -1) {
                substring = substring.substring(0, indexOf);
            }
            return substring.concat(STR_SUFFIX);
        }
    }
}
