import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
/**
 * class represents the Consumer of match queue
 * By Yunyi Chi
 */
public class MatchConsumer {
    private static ConcurrentHashMap<Integer, List<Integer>> matchMap = new ConcurrentHashMap<>();
    private final static String QUEUE_NAME = "match";
    private static String username = "admin";
    private static String password = "admin";
    private static String host = "34.200.87.197";
    private static final int NUM_THREADS = 100;
    private static Gson gson = new Gson();

        private static String virtualHost = "cherry_broker";
    private static int portNumber = 5672;
    /*
     * run match consumer in multi-threads
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
                        int swipee = body.getSwipee();
                        if (body.getLeftOrRight().equals("right")) {
                            if (!matchMap.containsKey(swiper)) {
                                matchMap.put(swiper, new ArrayList<>());
                            }
                            if (matchMap.get(swiper).size() < 100) {
                                matchMap.get(swiper).add(swipee);
                            }
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
