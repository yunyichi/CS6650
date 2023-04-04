
package org.example;
/**
 * Client
 * By Yunyi Chi
 * */

import java.io.IOException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class MultithreadedClient {
    private final static int NUMREQUESTS = 500000;
    private final static int NUMTHREADS = 100;
    private final static int NUMGETS = 5;

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
        ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();

        // Create and schedule GetThread
        GetThread getThread = new GetThread(dataGeneration, completed, countData);
        ScheduledFuture<?> getThreadHandle = scheduledExecutor.scheduleAtFixedRate(getThread, 0, 1, TimeUnit.SECONDS);
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
        System.out.println("");
        getThread.printLatencyStats();

    }

}
