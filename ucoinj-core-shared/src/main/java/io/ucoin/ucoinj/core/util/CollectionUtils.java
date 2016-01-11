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

import java.util.Collection;
import java.util.Iterator;

/**
 * Created by eis on 23/12/14.
 */
public class CollectionUtils {

    public static boolean isNotEmpty(Collection<?> coll) {
        return coll != null && coll.size() > 0;
    }

    public static boolean isEmpty(Collection<?> coll) {
        return coll == null || coll.size() == 0;
    }

    public static boolean isNotEmpty(Object[] coll) {
        return coll != null && coll.length > 0;
    }
    public static boolean isEmpty(Object[] coll) {
        return coll == null || coll.length == 0;
    }

    public static String join(final Object[] array) {
        return join(array, ", ");
    }

    public static String join(final Object[] array, final  String separator) {
        if (array == null || array.length == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder(array.length * 7);
        sb.append(array[0]);
        for (int i = 1; i < array.length; i++) {
            sb.append(separator);
            sb.append(array[i]);
        }
        return sb.toString();
    }

    public static String join(final Collection<?> collection) {
        return join(collection, ", ");
    }

    public static String join(final Collection<?> collection, final  String separator) {
        if (collection == null || collection.size() == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder(collection.size() * 7);
        Iterator<?> iterator = collection.iterator();
        sb.append(iterator.next());
        while (iterator.hasNext()) {
            sb.append(separator);
            sb.append(iterator.next());
        }
        return sb.toString();
    }

    public static int size(final Collection<?> collection) {
        return collection == null ? 0 : collection.size();
    }

    public static <E> E extractSingleton(Collection<E> collection) {
        if(collection != null && collection.size() == 1) {
            return collection.iterator().next();
        } else {
            throw new IllegalArgumentException("Can extract singleton only when collection size == 1");
        }
    }
}
