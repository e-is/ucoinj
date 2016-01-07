package io.ucoin.ucoinj.core.util;

/*
 * #%L
 * UCoin Java Client :: Core API
 * %%
 * Copyright (C) 2014 - 2015 EIS
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
 * Created by eis on 22/12/14.
 */
public class ObjectUtils {

    public static void checkNotNull(Object value) {
        if (value == null) {
            throw new NullPointerException();
        }
    }

    public static void checkNotNull(Object value, String message) {
        if (value == null) {
            throw new NullPointerException(message);
        }
    }


    public static void checkArgument(boolean value, String message) {
        if (!value) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void checkArgument(boolean value) {
        if (!value) {
            throw new IllegalArgumentException();
        }
    }

    public static boolean equals(Object o1, Object o2) {
        if (o1 == null && o2 == null) {
            return true;
        }
        if ((o1 != null && o2 == null) ||(o1 == null && o2 != null)) {
            return false;
        }
        return o1.equals(o2);
    }
}
