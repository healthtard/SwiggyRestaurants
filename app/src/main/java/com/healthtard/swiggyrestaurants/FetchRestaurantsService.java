package com.healthtard.swiggyrestaurants;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by pradeepjeswani on 20/10/16.
 */

public class FetchRestaurantsService extends IntentService {

    private String TAG = FetchRestaurantsService.class.getCanonicalName();
    public static final String BROADCAST_ACTION_STATE_CHANGE
            = "com.healthtard.SwiggyRestaurants.intent.action.STATE_CHANGE";
    public static final String EXTRA_REFRESHING
            = "com.healthtard.SwiggyRestaurants.intent.extra.REFRESHING";
    public static final String RESPONSE
            = "response";
    JSONObject jsonObject;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public FetchRestaurantsService(String name) {
        super(name);
    }

    public FetchRestaurantsService() {
        super("FetchRestaurantsService");

    }

    @Override
    protected void onHandleIntent(Intent intent) {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null || !(ni.isConnected())) {
            Log.w(TAG, "Not online, not regreshing.");
            sendStickyBroadcast(new Intent(BROADCAST_ACTION_STATE_CHANGE).putExtra(EXTRA_REFRESHING, false));
        }

        sendStickyBroadcast(new Intent(BROADCAST_ACTION_STATE_CHANGE).putExtra(EXTRA_REFRESHING, true));

        try {
            jsonObject = RemoteEndPointUtil.fetchResponse();
            if (jsonObject == null)
                throw new JSONException("Invalid parsed item json object");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (jsonObject != null) {
            Intent broadcastIntent = new Intent(BROADCAST_ACTION_STATE_CHANGE);
            broadcastIntent.putExtra(EXTRA_REFRESHING, false);
            broadcastIntent.putExtra(RESPONSE, jsonObject.toString());
            sendStickyBroadcast(broadcastIntent);
        }
        else
            sendStickyBroadcast(new Intent(BROADCAST_ACTION_STATE_CHANGE).putExtra(EXTRA_REFRESHING, false));
    }
}