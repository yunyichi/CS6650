/**
 * HTTP Servlet
 * By Yunyi Chi
 * */

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

@WebServlet(name = "Servlet", value = "/Servlet")
public class Servlet extends HttpServlet {
    private static final int MIN = 1;
    private static final int MAX1 = 5000;
    private static final int MAX2 = 1000000;
    private static final int NUM_CHANS = 100;
//    private static String username = "guest";
//    private static String password = "guest";
    private static String username = "admin";
    private static String password = "admin";
    private static String host = "34.200.87.197";
    private static String virtualHost = "cherry_broker";
    private static int portNumber = 5672;
    private RMQChannelPool pool;
    private static String likeQueue = "like";
    private static String matchQueue = "match";


    @Override
    public void init() throws ServletException {
        super.init();
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername(username);
        factory.setPassword(password);
        factory.setHost(host);
        factory.setPort(portNumber);
        factory.setVirtualHost(virtualHost);
        final Connection connection;
        try {
            connection = factory.newConnection();
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
        RMQChannelFactory channelFactory = new RMQChannelFactory(connection);
        pool = new RMQChannelPool(NUM_CHANS, channelFactory);

    }

    // handle Post request
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/plain");
        String urlPath = req.getPathInfo();

//        check if url is null or empty
        if (urlPath == null || urlPath.isEmpty()) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            res.getWriter().write("not found url path");
            return;
        }

        String[] urlParts = urlPath.split("/");


        if (!isUrlValid(urlParts)) {
            res.setStatus((HttpServletResponse.SC_NOT_FOUND));
            res.getWriter().write("url is not valid, should be left or right");
        }
        Gson gson = new Gson();
        SwipeDetails body = gson.fromJson(req.getReader(), SwipeDetails.class);
        if (!isBodyValid(body)) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().write("invalid request body");
        } else {
            res.setStatus((HttpServletResponse.SC_CREATED));
            res.getWriter().write("It works");
            Payload payload = new Payload(urlParts[2], body.getSwiper(), body.getSwipee(), body.getComment());
            String payloadString = gson.toJson(payload);
            sendPayloadToQueue(payloadString);
        }
    }
//    check if url is valid
    private boolean isUrlValid(String[] urlParts) {
        if (    urlParts[1].equals("swipe") &&
                (urlParts[2].equals("left") || urlParts[2].equals("right"))) {
            return true;
        }
        return false;
    }
// check if body valid
    public boolean isBodyValid(SwipeDetails body) {
        int swiper = body.getSwiper();
        int swipee = body.getSwipee();
        String comment = body.getComment();
        return swiper >= MIN && swiper <= MAX1 && swipee >= MIN && swipee <= MAX2 && comment.length() != 0 && comment.length() <= 256;
    }
    /*
     * send the Payload String to the remote queue
     */
    public void sendPayloadToQueue(String payload) {
           try {
               Channel channel;
               channel = pool.borrowObject();
               channel.queueDeclare(likeQueue, false, false,false, null);
               channel.queueDeclare(matchQueue, false, false,false, null);
               channel.basicPublish("", likeQueue, null, payload.getBytes());
               channel.basicPublish("", matchQueue, null, payload.getBytes());
               pool.returnObject(channel);
           } catch (Exception e) {
               throw new RuntimeException(e);
           }
    }
}
