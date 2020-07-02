package com.rasmitap.tailwebs_assigment2.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.rasmitap.tailwebs_assigment2.utils.ConstantStore;

public class LocationProviderChangedReceiver extends BroadcastReceiver {

    private final static String TAG = "LocationProviderChanged";

    boolean isGpsEnabled;
    boolean isNetworkEnabled;


    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().matches("android.location.PROVIDERS_CHANGED"))
        {
            Log.i(TAG, "Location Providers changed");

            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            boolean is_Location_on=false;
            if(isGpsEnabled && isNetworkEnabled){
                is_Location_on=true;
            }
            //Start your Activity if location was enabled:

                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(ConstantStore.LOCATION_ENABLE_BR).putExtra(ConstantStore.LOCATION_ENABKE_VALUE_KEY,is_Location_on));

        }
    }
}