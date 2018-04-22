package com.example.utils;

public interface LongStringUtils {

    int MAX_CHARS = 60;
    String STR_SUFFIX = " ...>";

    static String formatLongString(Object input) {
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
