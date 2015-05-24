package edu.sjsu.cmpe.cache.client;

import java.util.Map;

/**
 * Cache Service Interface
 * 
 */
public interface CacheServiceInterface {
    public String get(long key);

    public void put(long key, String value);

    public Map<String, Boolean> asyncPut(long key, String value);

    public void rollBackWrite(long key, Map<String, Boolean> status);

    public Map<String, String> asyncGet(long key);
}
