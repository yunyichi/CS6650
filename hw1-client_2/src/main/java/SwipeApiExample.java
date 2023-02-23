/**
 * Test Swagger API
 * By Yunyi Chi
 * */

import io.swagger.client.*;
import io.swagger.client.auth.*;
import io.swagger.client.model.*;
import io.swagger.client.api.SwipeApi;
import java.io.File;
import java.util.*;

public class SwipeApiExample {
    private static final String baseUrl = "http://localhost:8080/hw1_server_war_exploded/";

    public static void main(String[] args) {
        SwipeApi apiInstance = new SwipeApi();
        apiInstance.getApiClient().setBasePath(baseUrl);
        SwipeDetails body = new SwipeDetails();
        String leftorright = "right";
        try {
            ApiResponse res = apiInstance.swipeWithHttpInfo(body, leftorright);
            System.out.println(res.getStatusCode());
        } catch (ApiException e) {
            System.err.println("Exception when calling SwipeApi#swipe");
            e.printStackTrace();
        }
    }
}