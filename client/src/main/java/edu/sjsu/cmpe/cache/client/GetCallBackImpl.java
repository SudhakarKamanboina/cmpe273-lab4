package edu.sjsu.cmpe.cache.client;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.util.Map;

/**
 * Created by sudh on 5/23/2015.
 */
public class GetCallBackImpl implements Callback<JsonNode> {

    private final String serverUrl;
    private final Map<String, String> statusMap;

    public GetCallBackImpl(String serverUrl,Map<String,String> statusMap){
        this.serverUrl = serverUrl;
        this.statusMap = statusMap;
    }

    @Override
    public void completed(HttpResponse<JsonNode> httpResponse) {
        System.out.println("Completed call for node: "+this.serverUrl);
        if(httpResponse.getStatus()==200){
            JsonNode body = httpResponse.getBody();
            String value = body.getObject().getString("value");
            statusMap.put(serverUrl, value);
            System.out.println(String.format("Completed the get call for Node %s and obtained value %s", serverUrl,value));
        } else{
            System.out.println(String.format("Completed the get call for Node %s and did not obtain value", serverUrl));
            statusMap.put(serverUrl,"EMPTY");
        }
    }

    @Override
    public void failed(UnirestException e) {
        System.out.println(String.format("Failed with exception %s",e.getMessage()));
    }

    @Override
    public void cancelled() {
        System.out.println(String.format("Cancelled request to node %s",serverUrl));
    }
}
