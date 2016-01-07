package io.ucoin.ucoinj.core.util;

/**
 * Created by eis on 21/12/14.
 */
public class StringUtils {

    public static boolean isNotBlank(String value) {
        return value != null && value.trim().length() > 0;
    }

    public static boolean isBlank(String value) {
        return value == null || value.trim().length() == 0;
    }

    public static boolean isNotEmpty(String value) {
        return value != null && value.length() > 0;
    }

    public static boolean isEmpty(String value) {
        return value == null || value.length() == 0;
    }

    public static String truncate(String value, int maxLength) {
        if (value != null && value.length() > maxLength && maxLength >= 1) {
            return value.substring(0, maxLength - 1);
        }
        else {
            return value;
        }
    }

    public static String truncateWithIndicator(String value, int maxLength) {
        if (value != null && value.length() > maxLength && maxLength >= 4) {
            return value.substring(0, maxLength - 3) + "...";
        }
        else {
            return value;
        }
    }

    public static boolean equals(String cs1, String cs2) {
        return cs1 == null?cs2 == null:cs1.equals(cs2);
    }
}
