package org.example;
/**
 * Get Thread
 * By Yunyi Chi
 * */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

public class GetThread implements Runnable {
    private String url1 = "http://52.3.248.46:8080/GetMatchServer_war/matches/";
    private String url2 = "http://25.8.212.34:8080/GetStatsServer_war/stats/";
    private DataGeneration dataGeneration;
    private CountData countData;
    private CountDownLatch completed;
    private final CopyOnWriteArrayList<Long> latencies = new CopyOnWriteArrayList<>();
    private final Random random = new Random();

    public GetThread(DataGeneration dataGeneration, CountDownLatch completed, CountData countData) {
        this.dataGeneration = dataGeneration;
        this.completed = completed;
        this.countData = countData;
    }

    @Override
    public void run() {
        // Only perform GET requests if there are still pending posting threads
        if (completed.getCount() > 0) {
            for (int i = 0; i < 5; i++) {
                String requestUrl = random.nextInt(2) == 0 ? url1 : url2;
                requestUrl += dataGeneration.generateSwiperNumber();

                long startTime = System.currentTimeMillis();
                int responseCode = sendGetRequest(requestUrl);
                long endTime = System.currentTimeMillis();

                if (responseCode == HttpURLConnection.HTTP_CREATED) {
                    latencies.add(endTime - startTime);
                }
            }
        }
    }

    private int sendGetRequest(String requestUrl) {
        try {
            URL urlObj = new URL(requestUrl);
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_CREATED) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }

                in.close();
            }
            return responseCode;

        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void printLatencyStats() {
        if (latencies.isEmpty()) {
            System.out.println("No GET requests were made.");
            return;
        }

        long sum = 0;
        long min = Long.MAX_VALUE;
        long max = Long.MIN_VALUE;

        for (Long latency : latencies) {
            sum += latency;
            min = Math.min(min, latency);
            max = Math.max(max, latency);
        }

        double mean = (double) sum / latencies.size();

        System.out.println("GET Latencies:");
        System.out.println("Min: " + min + " millisecs");
        System.out.println("Mean: " + mean + " millisecs");
        System.out.println("Max: " + max + " millisecs");
    }
}
