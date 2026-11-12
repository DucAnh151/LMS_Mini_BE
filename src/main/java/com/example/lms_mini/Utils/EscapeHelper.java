package com.example.lms_mini.Utils;

public class EscapeHelper {
    public static String escapeLike(String param) {
        if (param == null) {
            return null;
        }
        return param.replace("\\", "\\\\")
                    .replace("%", "\\%")
                    .replace("_", "\\_");
    }
}
