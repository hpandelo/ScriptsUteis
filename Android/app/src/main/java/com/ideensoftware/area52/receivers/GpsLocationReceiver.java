package com.ideensoftware.area52.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ideensoftware.area52.MainActivity;


/**
 * Created by hpandelo on 19/11/15.
 */
public class GpsLocationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
                Intent pushIntent = new Intent(context, MainActivity.class);
                context.startService(pushIntent);

                Log.d("DEBUG/LocationReceiver", "ProviderChanged!");
            }
        }catch (Exception e){
            Log.d("DEBUG/LocationReceiver", "Unable to start: " + e.toString());
            e.printStackTrace();

        }
    }
}