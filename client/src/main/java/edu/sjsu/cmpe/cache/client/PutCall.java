package edu.sjsu.cmpe.cache.client;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;

import java.util.concurrent.Callable;

/**
 * Created by sudh on 5/23/2015.
 */
public class PutCall implements Callable<HttpResponse<JsonNode>> {

    private final String serverUrl;
    private final String key;
    private final String value;
    private final Callback callback;

    PutCall(String serverUrl, String key, String value, Callback callback)
    {
        this.serverUrl = serverUrl;
        this.key = key;
        this.value = value;
        this.callback = callback;
    }


    @Override
    public HttpResponse<JsonNode> call() throws Exception {
        System.out.println(String.format("Trying to put %s => %s in node %s",key,value,serverUrl));
        return (HttpResponse<JsonNode>) Unirest.put(this.serverUrl + "/cache/{key}/{value}")
                .header("accept", "application/json")
                .routeParam("key", key)
                .routeParam("value", value)
                .asJsonAsync(callback).get();
    }
}
