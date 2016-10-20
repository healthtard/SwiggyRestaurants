package com.healthtard.swiggyrestaurants;

/**
 * Created by pradeepjeswani on 24/08/16.
 */

import java.net.MalformedURLException;
import java.net.URL;

public class Config {
    public static final URL BASE_URL;

    static {
        URL url = null;
        try {
            url = new URL("https://api.myjson.com/bins/ngcc");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        BASE_URL = url;
    }
}
