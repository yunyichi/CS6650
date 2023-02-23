package org.example;

/**
 * Generate random data for post request
 * By Yunyi Chi
 * */
import io.swagger.client.*;
import io.swagger.client.auth.*;
import io.swagger.client.model.*;
import io.swagger.client.api.SwipeApi;
import java.util.Random;
import java.io.File;
import java.util.*;
public class DataGeneration {

    private static final int MIN = 1;
    private static final int MAX1 = 5000;

    private static final int MAX2 = 1000000;

    private static final String[] leftOrRight = new String[]{"left", "right"};
    public String generateLeftOrRight() {
        Random rand = new Random();
        return leftOrRight[rand.nextInt(2)];
    }

    public int generateSwiperNumber() {
        Random rand = new Random();
        return rand.nextInt((MAX1 - MIN) + 1) + MIN;
    }

    public int generateSwipeeNumber() {
        Random rand = new Random();
        return rand.nextInt((MAX2 - MIN) + 1) + MIN;
    }

    public String generateComments() {
        int n = 256;
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random rand = new Random();
        StringBuilder randomString = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            randomString.append(characters.charAt(rand.nextInt(characters.length())));
        }
        return randomString.toString();
    }

    public SwipeDetails generateBody() {
        SwipeDetails body = new SwipeDetails();
        body.setSwiper(String.valueOf(this.generateSwiperNumber()));
        body.setSwipee(String.valueOf(this.generateSwipeeNumber()));
        body.setComment(String.valueOf(this.generateComments()));
        return body;
    }

}
