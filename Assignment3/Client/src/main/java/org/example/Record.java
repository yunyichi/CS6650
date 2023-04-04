package org.example;
/**
 * Record to save data for per request
 * By Yunyi Chi
 * */
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Record {
    private long startTime;
    private String requestType;

    private long latency;
    private int responseCode;

    public Record() {
        this.startTime = 0;
        this.requestType = "";
        this.latency = 0;
        this.responseCode = 0;

    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public long getLatency() {
        return latency;
    }

    public void setLatency(long latency) {
        this.latency = latency;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }
}
