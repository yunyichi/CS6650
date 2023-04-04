package org.example;

/**
 * Calculated Data for Client (Part2)
 * By Yunyi Chi
 * */

import com.opencsv.CSVWriter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class CalculatePartTwo {
    private BlockingQueue<Record> resultQueue;
    private List<Long> latencyList;
    private long sumOfResponseTime;
    private double meanRequestTime;
    private long medianRequestTime;
    private long minRequestTime;
    private long maxRequestTime;
    private long totalNumberOfRequest;
    private double throughput;
    private long p99;
    private long wallTime;

    public CalculatePartTwo(BlockingQueue resultQueue, long wallTime) {
        this.resultQueue = resultQueue;
        this.latencyList = new ArrayList<>();
        this.sumOfResponseTime = 0;
        this.totalNumberOfRequest = resultQueue.size();
        this.minRequestTime = Long.MIN_VALUE;
        this.maxRequestTime = Long.MAX_VALUE;
        this.throughput = 0.0;
        this.p99 = 0;
        this.meanRequestTime = 0.0;
        this.medianRequestTime = 0;
        this.wallTime = wallTime;
    }

    public void run() throws InterruptedException, IOException {
        List<Integer> startTimeList = new ArrayList<>();
        File file = new File("data.csv");
        FileWriter outputfile = new FileWriter(file);
        CSVWriter writer = new CSVWriter(outputfile);
        String[] section = {"Request Type", "Status Code", "Start Time", "Latency"};
        writer.writeNext(section);
        while (!resultQueue.isEmpty()) {
            Record temp = resultQueue.take();
            startTimeList.add((int)((double)(temp.getStartTime()*1.0/1000)));
            String[] content = {temp.getRequestType(), String.valueOf(temp.getResponseCode()), String.valueOf(temp.getStartTime()), String.valueOf(temp.getLatency())};
            writer.writeNext(content);
            long time = temp.getLatency();
            sumOfResponseTime += time;
            this.latencyList.add(time);
        }
        writer.close();
        Collections.sort(latencyList);
        meanRequestTime = sumOfResponseTime/totalNumberOfRequest;
        if (totalNumberOfRequest%2 == 0) {
            medianRequestTime = (latencyList.get((int)(totalNumberOfRequest/2)) + latencyList.get((int)(totalNumberOfRequest/2)+1))/2;
        } else {
            medianRequestTime = latencyList.get((int)(totalNumberOfRequest/2) + 1);
        }

        double tempThroughput = totalNumberOfRequest/(wallTime*1.0/1000);
        BigDecimal bd = new BigDecimal(tempThroughput);
        this.throughput = bd.setScale(2, BigDecimal.ROUND_DOWN).doubleValue();


        p99 = latencyList.get((int)(totalNumberOfRequest*0.99));
        minRequestTime = latencyList.get(0);
        maxRequestTime = latencyList.get(latencyList.size()-1);
        Collections.sort(startTimeList);
        int maxStartTime = startTimeList.get(startTimeList.size()-1);
        int minStartTime = startTimeList.get(0);
        int length = maxStartTime - minStartTime + 1;
        int[] plots = new int[length];
        for (int i = 0; i < startTimeList.size(); i++) {
            int val = startTimeList.get(i);
            plots[val-minStartTime]++;
        }
        File plotFile = new File("part4.csv");
        FileWriter part4Output = new FileWriter(plotFile);
        CSVWriter part4Writer = new CSVWriter(part4Output);
        for (int i = 0; i < plots.length; i++) {
            String[] times = {String.valueOf(plots[i])};
            part4Writer.writeNext(times);
        }
        part4Writer.close();
    }

    public long getTotalNumberOfRequest() {
        return totalNumberOfRequest;
    }
    public long getWallTime() {
        return wallTime;
    }
    public double getMeanRequestTime() {
        return meanRequestTime;
    }

    public long getMedianRequestTime() {
        return medianRequestTime;
    }

    public long getMinRequestTime() {
        return minRequestTime;
    }

    public long getMaxRequestTime() {
        return maxRequestTime;
    }

    public double getThroughput() {
        return throughput;
    }

    public long getP99() {
        return p99;
    }
}
