
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
import java.io.IOException;
import java.util.Random;

import java.io.File;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class MultithreadedClient {
    private final static int NUMREQUESTS = 500000;
    private static final String baseUrl = "http://44.202.188.179:8080/hw1_server_war";
//    private static final String baseUrl = "http://localhost:8080/hw1_server_war_exploded/";
    private final static int NUMTHREADS = 100;

    public static void main(String[] args) throws InterruptedException, IOException {
        CountData countData = new CountData();
        CountDownLatch completed = new CountDownLatch(NUMREQUESTS);
        DataGeneration dataGeneration = new DataGeneration();
        ExecutorService executor = Executors.newFixedThreadPool(NUMTHREADS);
        BlockingQueue<Record> resultQueue = new LinkedBlockingQueue();
        countData.setStartTime(new AtomicLong(System.currentTimeMillis()));

        for (int i = 0; i < NUMTHREADS; i++) {
            executor.execute(new SingleThread(dataGeneration, completed, countData, resultQueue));
        }
        completed.await();
        executor.shutdown();
        Long end = System.currentTimeMillis();
        countData.setEndTime(new AtomicLong(System.currentTimeMillis()));
        countData.calculateTotalRunTime();
        countData.calculateThroughput();


        System.out.println("Client Part 1 Data:");
        System.out.println("Number of successful requests is: " + countData.getNumOfSuccessfulRequest());
        System.out.println("Number of Unsuccessful requests is: " + countData.getNumOfFailedRequest());
        System.out.println("Total run time is: " + countData.getTotalRunTime() + " ms");
        System.out.println("Throughput is : " + countData.getThroughput() + " requests/second");
        System.out.println();

        CalculatePartTwo part2 = new CalculatePartTwo(resultQueue, countData.getTotalRunTime().get());
        part2.run();
        System.out.println("Client Part 2 Data:");
        System.out.println("Mean response Time is : " + part2.getMeanRequestTime() + " millisecs");
        System.out.println("Median request Time is : " + part2.getMedianRequestTime() + " millisecs");
        System.out.println("throughPut is : " + part2.getThroughput() + " requests/second");
        System.out.println("p99 response time is : " + part2.getP99() + " millisecs");
        System.out.println("Min response Time is : " + part2.getMinRequestTime() + " millisecs");
        System.out.println("Max response Time is : " + part2.getMaxRequestTime() + " millisecs");

    }

}
