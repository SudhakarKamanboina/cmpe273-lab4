package edu.sjsu.cmpe.cache.client;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Distributed cache service
 * 
 */
public class DistributedCacheService implements CacheServiceInterface {
    private final String cacheServerUrl;
    private final List<String> serverUrls;

    public DistributedCacheService(String serverUrl, List<String> serverUrls) {
        this.cacheServerUrl = serverUrl;
        this.serverUrls = serverUrls;
    }

    /**
     * @see edu.sjsu.cmpe.cache.client.CacheServiceInterface#get(long)
     */
    @Override
    public String get(long key) {
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.get(this.cacheServerUrl + "/cache/{key}")
                    .header("accept", "application/json")
                    .routeParam("key", Long.toString(key)).asJson();
        } catch (UnirestException e) {
            System.err.println(e);
        }
        String value = response.getBody().getObject().getString("value");

        return value;
    }

    /**
     * @see edu.sjsu.cmpe.cache.client.CacheServiceInterface#put(long,
     *      java.lang.String)
     */
    @Override
    public void put(long key, String value) {
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest
                    .put(this.cacheServerUrl + "/cache/{key}/{value}")
                    .header("accept", "application/json")
                    .routeParam("key", Long.toString(key))
                    .routeParam("value", value).asJson();
        } catch (UnirestException e) {
            System.err.println(e);
        }

        if (response.getStatus() != 200) {
            System.out.println("Failed to add to the cache.");
        }
    }

    @Override
    public Map<String, Boolean> asyncPut(long key, String value) {
        ExecutorService service = Executors.newFixedThreadPool(10);

        List<PutCall> calls = new ArrayList<PutCall>();
        Map<String, Boolean> statusMap = new ConcurrentHashMap<String, Boolean>();

        for(String url : serverUrls)
        {
            statusMap.put(url, false);
            PutCallBackImpl callBack = new PutCallBackImpl(url, statusMap);
            calls.add(new PutCall(url, Long.toString(key), value, callBack));
        }

        try {
            service.invokeAll(calls);
            service.awaitTermination(1000, TimeUnit.MILLISECONDS);
            service.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return statusMap;
    }

    @Override
    public void rollBackWrite(long key, Map<String, Boolean> statusMap) {
        HttpResponse<JsonNode> response = null;
        try {
            System.out.println("Starting Rollback");
            for (String server:serverUrls){
                if (statusMap.get(server)){
                    System.out.println(String.format("Deleting key %s from Node: %s",key,server));
                    response = Unirest.delete(server + "/cache/{key}")
                            .routeParam("key", Long.toString(key)).asJson();
                }
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    @Override
    public Map<String, String> asyncGet(long key) {
        ExecutorService executorService = Executors.newFixedThreadPool(3);

        List<GetCall> calls = new ArrayList<GetCall>();
        Map<String,String> statusMap = new ConcurrentHashMap<String, String>(3);

        for (String serverUrl: serverUrls){
            GetCallBackImpl callback = new GetCallBackImpl(serverUrl, statusMap);
            calls.add(new GetCall(serverUrl, Long.toString(key), callback));
        }
        try {
            executorService.invokeAll(calls);
            executorService.awaitTermination(15000, TimeUnit.MILLISECONDS);
            executorService.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return statusMap;
    }
}
