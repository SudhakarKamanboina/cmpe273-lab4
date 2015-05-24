package edu.sjsu.cmpe.cache.client;

import java.util.Arrays;
import java.util.List;

public class Client {

    public static void main(String[] args) throws Exception {
        System.out.println("Starting Cache Client...");
        List<String> servers = Arrays.asList("http://localhost:3000", "http://localhost:3001", "http://localhost:3002");

        System.out.println("Initiating step 1 -- Writing to Nodes and verifying majority writes as per PART 1 requirements");
        final CRDTClient crdtClient = new CRDTClient(servers);

        crdtClient.put(1, "a");

        sleepFor(30000);

        crdtClient.put(1,"b");

        sleepFor(30000);

        System.out.println("Initiating step 2 -- Read node values for read repair operation");
        crdtClient.get(1);
        System.out.println("Existing Cache Client...");
    }

    private static void sleepFor(int sleepInMillis) {
        try {
            System.out.println("Sleeping for " + (sleepInMillis / 1000)+"seconds");
            Thread.sleep(sleepInMillis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
