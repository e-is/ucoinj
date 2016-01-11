package io.ucoin.ucoinj.core.util;

/*
 * #%L
 * UCoin Java :: Core Shared
 * %%
 * Copyright (C) 2014 - 2016 EIS
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

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
