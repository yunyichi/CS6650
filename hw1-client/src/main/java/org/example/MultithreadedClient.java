
package org.example;
/**
 * Client
 * By Yunyi Chi
 * */

import io.swagger.client.*;
import io.swagger.client.auth.*;
import io.swagger.client.model.*;
import io.swagger.client.api.SwipeApi;
import javax.xml.crypto.Data;
import java.util.Random;
import java.io.File;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

public class MultithreadedClient {
    private final static int NUMREQUESTS = 500000;
    private static final String baseUrl = "http://44.202.188.179:8080/hw1_server_war";
//    private static final String baseUrl = "http://localhost:8080/hw1_server_war_exploded/";
    private final static int NUMTHREADS = 100;

    public static void main(String[] args) throws InterruptedException{
        CountData countData = new CountData();
        CountDownLatch completed = new CountDownLatch(NUMREQUESTS);
        DataGeneration dataGeneration = new DataGeneration();
        ExecutorService executor = Executors.newFixedThreadPool(NUMTHREADS);
        countData.setStartTime(new AtomicLong(System.currentTimeMillis()));

        for (int i = 0; i < NUMTHREADS; i++) {
            executor.execute(new SingleThread(dataGeneration, completed, countData));
        }
        completed.await();
        executor.shutdown();
        Long end = System.currentTimeMillis();
        countData.setEndTime(new AtomicLong(System.currentTimeMillis()));
        countData.calculateTotalRunTime();
        countData.calculateThroughput();

        System.out.println("Number of successful requests is: " + countData.getNumOfSuccessfulRequest());
        System.out.println("Number of Unsuccessful requests is: " + countData.getNumOfFailedRequest());
        System.out.println("Total run time is: " + countData.getTotalRunTime() + " ms");
        System.out.println("Throughput is : " + countData.getThroughput() + " requests/second");

    }

}
