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

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by eis on 30/03/15.
 */
public abstract class SimpleCache<K, V> implements Cache<K, V> {

    private static final long ETERNAL_TIME = -1l;
    private static final long ILLIMITED_ITEMS_COUNT = -1l;
    private static final long ITEMS_COUNT_FOR_DEFAULT_CLEANING = 1000;

    private final Map<K, V> mCachedValues;
    private final Map<K, Long> mCachedTimes;
    private final long mCacheTimeInMillis;
    private final long mCacheMaxItemCount;

    final Object      mutex;

    public SimpleCache() {
        this(ETERNAL_TIME, ILLIMITED_ITEMS_COUNT);
    }

    public SimpleCache(long cacheTimeInMillis) {
        this(cacheTimeInMillis, ILLIMITED_ITEMS_COUNT);
    }

    public SimpleCache(long cacheTimeInMillis, long cacheMaxItemsCount) {
        this.mCachedValues = Collections.synchronizedMap(new ConcurrentHashMap<K, V>());
        this.mCachedTimes = new ConcurrentHashMap<K, Long>();
        this.mCacheTimeInMillis = cacheTimeInMillis;
        this.mCacheMaxItemCount = cacheMaxItemsCount;
        this.mutex = mCachedValues;
    }

    public V getIfPresent(K key) {
        synchronized (mutex) {
            V cachedValue = mCachedValues.get(key);
            long timeInMillis = System.currentTimeMillis();
            if (cachedValue != null) {
                Long cachedTime = mCachedTimes.get(key);
                if (mCacheTimeInMillis == ETERNAL_TIME
                        || cachedTime.longValue() - timeInMillis < mCacheTimeInMillis) {
                    return cachedValue;
                }
            }
            return null;
        }
    }

    /**
     * Get the cached value. If not already loaded, <code>load()</code>
     * will be called.
     * @param key
     * @return
     */
    public V get(K key) {
        synchronized (mutex) {
            V cachedValue = mCachedValues.get(key);
            long timeInMillis = System.currentTimeMillis();
            if (cachedValue != null) {
                Long cachedTime = mCachedTimes.get(key);
                if (timeInMillis - cachedTime.longValue() < mCacheTimeInMillis) {
                    return cachedValue;
                }
            }

            // Load a new value
            cachedValue = load(key);

            // Fill caches
            mCachedValues.put(key, cachedValue);
            mCachedTimes.put(key, timeInMillis);

            return cachedValue;
        }
    }

    /**
     * Set a value into the cache
     * @param key
     * @param value
     */
    public synchronized void put(K key, V value) {
        synchronized (mutex) {
            // Fill caches
            mCachedValues.put(key, value);
            mCachedTimes.put(key, System.currentTimeMillis());

            clean();
        }
    }

    /**
     * @see Map#keySet()
     */
    public Set<K> keySet() {
        return mCachedValues.keySet();
    }

    /**
     * @see Map#entrySet()
     */
    public Set<Map.Entry<K,V>> entrySet() {
        return mCachedValues.entrySet();
    }

    /**
     * Clear cached values
     */
    public void clear() {
        synchronized (mutex) {
            mCachedValues.clear();
            mCachedTimes.clear();
        }
    }

    public abstract V load(K key);

    /** -- protected methods -- */

    protected void clean() {
        synchronized (mutex) {
            // Clean older items, to fit the max size
            if (mCacheMaxItemCount != ILLIMITED_ITEMS_COUNT
                    && mCachedValues.size() > mCacheMaxItemCount) {

                // Clean too old items
                if (mCacheTimeInMillis != ETERNAL_TIME) {
                    removeOldItems();
                }

                // Size still exceed max: clean older items also
                if (mCachedValues.size() > mCacheMaxItemCount) {
                    reduceSizeToExpectedMaxItemCount();
                }
            } else if (mCacheMaxItemCount == ILLIMITED_ITEMS_COUNT
                    && mCachedValues.size() > ITEMS_COUNT_FOR_DEFAULT_CLEANING) {
                removeOldItems();
            }
        }
    }

    /** -- private methods -- */

    /**
     * Remove items older than <code>cacheTimeInMillis</code>
     */
    private void removeOldItems() {
        long now = System.currentTimeMillis();

        List<K> keysToRemove = new ArrayList<>();
        for (Map.Entry<K, Long> entry : mCachedTimes.entrySet()) {
            if (now - entry.getValue() > mCacheTimeInMillis) {
                keysToRemove.add(entry.getKey());
            }
        }

        for (K key : keysToRemove) {
            mCachedValues.remove(key);
            mCachedTimes.remove(key);
        }
    }

    /**
     * Reduce size of items: will first remove older items
     */
    private void reduceSizeToExpectedMaxItemCount() {

        K keyToRemove = null;
        Long olderTime = null;

        while (mCachedValues.size() > mCacheMaxItemCount) {

            K olderKey = getOlderKey();
            mCachedValues.remove(olderKey);
            mCachedTimes.remove(olderKey);
        }
    }

    private K getOlderKey() {

        K olderKey = null;
        long olderTime = -1;

        for (Map.Entry<K, Long> entry : mCachedTimes.entrySet()) {
            if (olderTime == -1 || entry.getValue() < olderTime) {
                olderTime = entry.getValue();
                olderKey = entry.getKey();
            }
        }

        return olderKey;
    }
}
