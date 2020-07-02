package com.rasmitap.tailwebs_assigment2.Location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

 // code refere link
// https://codelabs.developers.google.com/codelabs/realtime-asset-tracking/index.html?index=..%2F..%2Findex#2

 //*/

// NOT IN USE

public class FirebaseLocationUpdates {


    private static String firebase_path;
    private static final String TAG = "FirebaseLocationUpdates";
    private static int count = 0;

   private static String mParentLBL_1="drivers";
    private static String mKeyDID="driver_id";
    private static String mKeyLat="latitude";
    private static String mKeyLong="longitude";
    private static String iDriverId="";




    public static void requestLocationUpdatesDriver(Context context, final String mDriverLBL_1, final String mLat, final String mLong) {


        LocationRequest request = new LocationRequest();
        request.setInterval(10000);
        request.setFastestInterval(5000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(context);

        final String path = mParentLBL_1 + "/" ;

        count++;

        int permission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (permission == PackageManager.PERMISSION_GRANTED) {
            // Request location updates and when an update is
            // received, store the location in Firebase

            client.requestLocationUpdates(request, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {

                    String chails = String.valueOf(iDriverId);
//                    String chails="DRI13129";
                   // DatabaseReference ref = FirebaseDatabase.getInstance().getReference(path).child(chails);

                    Location location = locationResult.getLastLocation();

                    String nnn= String.valueOf(locationResult.getLastLocation().getLatitude());
                    String nnn2= String.valueOf(locationResult.getLastLocation().getLongitude());


                    Log.d(TAG, "location update " + nnn+"  <<nnn - nnn2>> "+nnn2);

                    if (location != null) {
                        Log.d(TAG, "location update " + location);
//                        ref.setValue(location);
//                        ref.child(mKeyDID).setValue(iDriverId);
//                        ref.child(mKeyLat).setValue(mLat);
//                        ref.child(mKeyLong).setValue(mLong);
                    }

                }
            }, null);
        }
    }


}






















//
//    private static final String TAG = "TrackerActivity";
//    int count=0;
//    private void requestLocationUpdates() {
//        LocationRequest request = new LocationRequest();
//        request.setInterval(10000);
//        request.setFastestInterval(5000);
//        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
//
//        final String path = getString(R.string.firebase_path) + "/" + "drivers"+"/";
////        final String path = getString(R.string.firebase_path) + "/" + getString(R.string.transport_id);
//
//        count++;
//
//        int permission = ContextCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_FINE_LOCATION);
//
//        if (permission == PackageManager.PERMISSION_GRANTED) {
//            // Request location updates and when an update is
//            // received, store the location in Firebase
//
//            client.requestLocationUpdates(request, new LocationCallback() {
//                @Override
//                public void onLocationResult(LocationResult locationResult) {
//
//                    String chails= String.valueOf(count);
////                    String chails="DRI13129";
//                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference(path).child(chails);
//
//                    Location location = locationResult.getLastLocation();
//
//                    if (location != null) {
//                        Log.d(TAG, "location update " + location);
////                        ref.setValue(location);
//                        ref.child("DriverId").setValue("DRI13129");
//                        ref.child("Latitude").setValue("23.12345678");
//                        ref.child("Longotude").setValue("23.12345678");
////                        ref.setValue("DriverId","DRI13128");
////                        ref.setValue("Latitude","23.12345678");
////                        ref.setValue("Longotude","75.12345678");
//
//                    }
//
//                }
//            }, null);
//        }
//    }
