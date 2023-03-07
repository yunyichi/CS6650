package org.example;

/**
 * Count Data for Client1
 * By Yunyi Chi
 * */

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class CountData {
    private AtomicInteger numOfSuccessfulRequest;
    private AtomicLong startTime;
    private AtomicLong endTime;
    private AtomicInteger numOfFailedRequest;
    private AtomicLong totalRunTime;
    private double throughput;

    public CountData() {
        this.numOfSuccessfulRequest = new AtomicInteger(0);
        this.startTime = new AtomicLong(0);
        this.endTime = new AtomicLong(0);
        this.numOfFailedRequest = new AtomicInteger(0);
        this.totalRunTime = new AtomicLong(0);
        this.throughput = 0.0;
    }

    public AtomicLong getStartTime() {
        return startTime;
    }

    public AtomicLong getEndTime() {
        return endTime;
    }

    public void setStartTime(AtomicLong startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(AtomicLong endTime) {
        this.endTime = endTime;
    }

    public AtomicInteger getNumOfSuccessfulRequest() {
        return numOfSuccessfulRequest;
    }

    public void incNumOfSuccessfulRequest(int val) {
        numOfSuccessfulRequest.addAndGet(val);
    }

    public AtomicInteger getNumOfFailedRequest() {
        return numOfFailedRequest;
    }

    public void incNumOfFailedRequest(int val) {
        numOfFailedRequest.addAndGet(val);
    }

    public AtomicLong getTotalRunTime() {
        return totalRunTime;
    }

    public void calculateTotalRunTime() {
        this.totalRunTime.set(this.endTime.get() - this.startTime.get());
    }


    public void calculateThroughput() {
        double temp = (numOfSuccessfulRequest.get() + numOfFailedRequest.get()) / ((totalRunTime.get()*1.0)/1000);
        BigDecimal bd = new BigDecimal(temp);
        this.throughput = bd.setScale(2, BigDecimal.ROUND_DOWN).doubleValue();

    }
    public double getThroughput() {
        return throughput;
    }
}
