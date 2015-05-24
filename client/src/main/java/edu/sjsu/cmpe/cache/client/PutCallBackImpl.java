package edu.sjsu.cmpe.cache.client;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.util.Map;

/**
 * Created by sudh on 5/23/2015.
 */
public class PutCallBackImpl implements Callback<JsonNode> {

    private final String serverUrl;
    private final Map<String, Boolean> statusMap;

    PutCallBackImpl(String serverUrl, Map<String, Boolean> statusMap)
    {
        this.serverUrl = serverUrl;
        this.statusMap = statusMap;
    }

    @Override
    public void completed(HttpResponse<JsonNode> httpResponse) {
        if(httpResponse.getStatus()==200){
            System.out.println("Request Succeeded for node "+ serverUrl);
            statusMap.put(serverUrl,true);
        }else{
            statusMap.put(serverUrl,false);
        }
    }

    @Override
    public void failed(UnirestException e) {
        System.out.println("Request Failed to node with url "+ serverUrl);
        statusMap.put(serverUrl, false);
    }

    @Override
    public void cancelled() {
        System.out.println("The request has been cancelled");
    }
}
