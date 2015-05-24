package edu.sjsu.cmpe.cache.client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by sudh on 5/23/2015.
 */
public class Test {

    public static void main(String args[])
    {
        ConcurrentHashMap<Long, String> test = new ConcurrentHashMap<Long, String>();
        test.put(Long.valueOf("1"), "a");
        test.put(Long.valueOf("1"), "b");

        String key1 ="1";
        String key2="1";

        System.out.println(test.get(Long.valueOf("1")));
    }
}
