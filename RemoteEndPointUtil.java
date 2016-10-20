package com.healthtard.swiggyrestaurants;

/**
 * Created by pradeepjeswani on 24/08/16.
 */

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RemoteEndPointUtil {
    public static final String TAG = RemoteEndPointUtil.class.getSimpleName();

    public static JSONObject fetchResponse() {

        //To get the json items string and handle exception
        String itemsJson = null;
        try {
            itemsJson = fetchPlainText(Config.BASE_URL);
        } catch (IOException e) {
            Log.e(TAG, "Error fetching items JSON", e);
            return null;
        }

        //Parse Json
        JSONTokener jsonTokener = new JSONTokener(itemsJson);

        Object val = null;
        try {
            val = jsonTokener.nextValue();
            if (!(val instanceof JSONObject))
                throw new JSONException("Expexted JsonObject");
            return (JSONObject) val;
        } catch (JSONException e) {
            Log.e(TAG,"Error parsing items JSON", e);
        }
        return null;
    }

    //To fetch the json string
    private static String fetchPlainText(URL url) throws IOException {
        OkHttpClient httpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = httpClient.newCall(request).execute();
        String body = response.body().string().toString();
        return body;
    }
}
