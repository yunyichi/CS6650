package org.example;
/**
 * Single thread
 * By Yunyi Chi
 * */
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.SwipeApi;
import io.swagger.client.model.SwipeDetails;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class SingleThread implements Runnable{
    private static final String baseUrl = "http://44.202.188.179:8080/hw1_server_war/";
//    private static final String baseUrl = "http://localhost:8080/hw1_server_war_exploded/";
    private DataGeneration dataGeneration;
    private CountDownLatch completed;
    private CountData countData;
    BlockingQueue resultQueue;

    private static final int MAXREQUEST= 5000;
    private static final int maxTry = 5;
    public SingleThread(DataGeneration dataGeneration, CountDownLatch completed, CountData countData, BlockingQueue resultQueue) {
        this.dataGeneration = dataGeneration;
        this.completed = completed;
        this.countData = countData;
        this.resultQueue = resultQueue;
    }

    @Override
    public void run() {
        SwipeApi apiInstance = new SwipeApi();
        apiInstance.getApiClient().setBasePath(baseUrl);
        SwipeDetails body = dataGeneration.generateBody();
        String leftOrRight = dataGeneration.generateLeftOrRight();
        int success = 0;
        int fail = 0;
        int count = 0;
        while (count < MAXREQUEST) {
            Record record = new Record();
            long startTime = System.currentTimeMillis();
            record.setStartTime(startTime);
            int responseCode = 0;
            for (int i = 0; i < maxTry; i++) {
                try {
                    ApiResponse res = apiInstance.swipeWithHttpInfo(body, leftOrRight);
                    responseCode = res.getStatusCode();
                    record.setResponseCode(responseCode);
                    success++;
                    break;
                } catch (ApiException e) {
                    if (i == maxTry - 1) {
                        responseCode = e.getCode();
                        record.setResponseCode(responseCode);
                        fail++;
                    }
                }
            }
            long endTime = System.currentTimeMillis();
            long latency = endTime - record.getStartTime();
            record.setLatency(latency);
            record.setRequestType("Post");

            try {
                resultQueue.put(record);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            count++;
            completed.countDown();
        }
        countData.incNumOfSuccessfulRequest(success);
        countData.incNumOfFailedRequest(fail);

    }
}
