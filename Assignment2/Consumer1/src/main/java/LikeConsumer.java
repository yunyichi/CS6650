import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * class represents the Consumer of like queue
 * By Yunyi Chi
 */

public class LikeConsumer {
    private static ConcurrentHashMap<Integer, Integer> likeMap = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<Integer, Integer> dislikeMap = new ConcurrentHashMap<>();
    private final static String QUEUE_NAME = "like";
    private static String username = "admin";
    private static String password = "admin";
    private static String host = "34.200.87.197";
    private static final int NUM_THREADS = 100;
    private static Gson gson = new Gson();

        private static String virtualHost = "cherry_broker";
    private static int portNumber = 5672;
    /*
     * run like consumer in multi-threads
     */
    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername(username);
        factory.setPassword(password);
        factory.setHost(host);
        factory.setVirtualHost(virtualHost);
        factory.setPort(portNumber);
        Connection connection = factory.newConnection();
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                final Channel channel = connection.createChannel();
                boolean durable = false;
                channel.queueDeclare(QUEUE_NAME, durable, false, false, null);
                channel.basicQos(1);
                DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                    String message = new String(delivery.getBody(), "UTF-8");
                    Payload body = gson.fromJson(message, Payload.class);
                    int swiper = body.getSwiper();
                    if (body.getLeftOrRight().equals("right")) {
                        if (!likeMap.containsKey(swiper)) {
                            likeMap.put(swiper, 0);
                        }
                        likeMap.put(swiper, likeMap.get(swiper)+ 1);
                    } else {
                        if (!dislikeMap.containsKey(swiper)) {
                            dislikeMap.put(swiper, 0);
                        }
                        dislikeMap.put(swiper, dislikeMap.get(swiper) + 1);
                    }
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);

                };
                boolean autoAck = false;
                channel.basicConsume(QUEUE_NAME, autoAck, deliverCallback, consumerTag -> { });
            } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        for (int i = 0; i < NUM_THREADS; i++) {
            new Thread(runnable).start();
        }
}
}
