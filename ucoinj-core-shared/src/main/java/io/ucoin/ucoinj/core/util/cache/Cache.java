package io.ucoin.ucoinj.core.util.cache;

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
