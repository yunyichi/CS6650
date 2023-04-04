import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * class represents the Consumer of like queue
 * By Yunyi Chi
 */

public class LikeConsumer {
    static StatsDao statsDao = new StatsDao();
    private static final int BATCH_SIZE = 100;
    private static final String QUEUE_NAME = "like";
    private static final int NUM_THREADS = 100;

    private static String username = "admin";
    private static String password = "admin";
    private static String host = "18.214.231.65";
    private static String virtualHost = "cherry_broker";
    private static int portNumber = 5672;
    private static Gson gson = new Gson();

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername(username);
        factory.setPassword(password);
        factory.setHost(host);
        factory.setVirtualHost(virtualHost);
        factory.setPort(portNumber);

        Connection connection = factory.newConnection();
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        for (int i = 0; i < NUM_THREADS; i++) {
            new Thread(() -> {
                try {
                    final Channel channel = connection.createChannel();
                    boolean durable = false;
                    channel.queueDeclare(QUEUE_NAME, durable, false, false, null);
                    channel.basicQos(BATCH_SIZE);
                    List<Payload> batch = new ArrayList<>();

                    DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                        String message = new String(delivery.getBody(), "UTF-8");
                        Payload body = gson.fromJson(message, Payload.class);
                        batch.add(body);

                        if (batch.size() == BATCH_SIZE) {
//                            processBatchAndUpdateDatabase(statsDao, batch);
                            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), true);
                            batch.clear();
                        }
                    };

                    boolean autoAck = false;
                    channel.basicConsume(QUEUE_NAME, autoAck, deliverCallback, consumerTag -> { });
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
    }

    private static void processBatchAndUpdateDatabase(StatsDao statsDao, List<Payload> batch) {
        // Process the batch and update the database.
        statsDao.createStats(batch);
    }
}









