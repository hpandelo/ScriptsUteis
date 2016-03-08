package com.ideensoftware.area52.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.ideensoftware.area52.GPS;


/**
 * This Receiver class is used to listen for Broadcast Intents that announce
 * that a location change has occurred while this application isn't visible.
 *
 * Where possible, this is triggered by a Passive Location listener.
 */
public class PassiveLocationChangedReceiver extends BroadcastReceiver {

    /**
     * When a new location is received, extract it from the Intent and use
     * it to start the Service used to update the list of nearby places.
     *
     * This is the Passive receiver, used to receive Location updates from
     * third party apps when the Activity is not visible.
     */
    @Override
    public void onReceive(Context context, Intent intent) {

        try {
            String key = LocationManager.KEY_LOCATION_CHANGED;
            Location location = null;

            GPS gps = new GPS(context);
            Log.d("DEBUG/GPS", gps.getLocation().toString());

        }catch (Exception e){
            Log.d("DEBUG/GPS", "Unable to start: " + e.toString());
            e.printStackTrace();

        }

    }
}
