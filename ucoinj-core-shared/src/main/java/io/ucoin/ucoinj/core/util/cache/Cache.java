package io.ucoin.ucoinj.core.util.cache;

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

import java.util.Map;
import java.util.Set;

/**
 * Created by eis on 30/03/15.
 */
public interface Cache<K, V> {

    /**
     * Get a cached instance, only if present.
     * @param key
     * @return the cached object if present, or null
     */
    V getIfPresent(K key);

    /**
     * Get the cached value. If not already loaded, <code>load()</code>
     * will be called.
     * @param key
     * @return
     */
    V get(K key);

    /**
     * Set a value into the cache
     * @param key
     * @param value
     */
    void put(K key, V value);

    /**
     * @see Map#keySet()
     */
    Set<K> keySet();

    /**
     * @see Map#entrySet()
     */
    Set<Map.Entry<K,V>> entrySet();

    /**
     * Clear cached values
     */
    void clear();

    V load(K key);

}
