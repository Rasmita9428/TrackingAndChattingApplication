package com.rasmitap.tailwebs_assigment2.Location;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.rasmitap.tailwebs_assigment2.view.MapActivity;


/**
 * Created by mind on 12/4/17.
 */

public class BackgroundService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    String token ="",driver_id="";
    SharedPreferences sharedPreferences;
    private Handler mHandler;
    GPSTracker gps;
    // default interval for syncing data
    public static final long DEFAULT_SYNC_INTERVAL = 30 * 1000;

    String miDriverId;


    GeoFire geoFire;

//    DatabaseReference reference;
    //    GeoFire geoFire;
    Context context;

    // geofire
    private static final int MY_PERMISSION_REQUEST_CODE = 7192;
    private static final int PLAY_SERVICE_RESOLUTION_REQUEST = 300193;

    private LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;
    private Location mLastLocation;
    int updateInterval = 5000;
    int fastInterval = 3000;
    int Displacement = 10;


    String mDRIVER = "Newdrivers", mDRIVERID, mLATITUDElbl = "latitude", mLONGITUDElbl = "longitude", mDRIVERIDlbl="driver_id",mLATITUDE, mLONGITUDE;
//    drivers





    // task to be run here
    private Runnable runnableService = new Runnable() {
        @Override
        public void run() {
            syncData();
            // Repeat this runnable code block again every ... min
            mHandler.postDelayed(runnableService, 8000);
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Create the Handler object
        mHandler = new Handler();
        // Execute a runnable task as soon as possible
        mHandler.post(runnableService);

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        gps = new GPSTracker(getApplication());



      //  geoFire = new GeoFire(reference);

//        sharedPreferences= getSharedPreferences("LOGIN_DETAIL", 0);
//        token =sharedPreferences.getString("vToken","");
//        driver_id =sharedPreferences.getString("DRIVERID","");
       // Toast.makeText(getApplicationContext(),"Oncreat Service",Toast.LENGTH_LONG).show();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    private synchronized void syncData() {
        Toast.makeText(this, "call service", Toast.LENGTH_LONG).show();


        // call your rest service here

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
        mHandler.removeCallbacks(runnableService);
        // Toast.makeText(getApplicationContext(),"OnDestroy Service",Toast.LENGTH_LONG).show();
    }


//
//    public void requestLocationUpdatesDrivers() {
//
//        reference = FirebaseDatabase.getInstance().getReference(mDRIVER);
//        GeoFire geoFire = new GeoFire(reference);
//
////        setupLocation();
//
//    }
//
//
//    public void setupLocation(GeoFire geoFire) {
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions((HomeActivity)context, new String[]{
//                    Manifest.permission.ACCESS_FINE_LOCATION,
//                    Manifest.permission.ACCESS_FINE_LOCATION
//            }, MY_PERMISSION_REQUEST_CODE);
//
//        } else {
//            if (CheckPlayService()) {
//
//                buildGoogleApiClient();
//                createLocationRequest();
//                displayLocation(geoFire);
//
//            }
//
//
//        }
//
//    }


    private void buildGoogleApiClient() {

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }



    public void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(updateInterval);
        locationRequest.setFastestInterval(fastInterval);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(Displacement);

    }



//
//    private void displayLocation(GeoFire geoFire) {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//
////        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
//        if (mLastLocation != null) {
//
//            final double Latitudes = mLastLocation.getLatitude();
//            final double Longotudes = mLastLocation.getLongitude();
//
//            final String lats,longs;
//
//            lats= String.valueOf(Latitudes);
//            longs= String.valueOf(Longotudes);
//
//            /////update in firebase
//
////            geoFire.setLocation("DRI1234", new GeoLocation(Latitudes, Longotudes));
//
////            user user=new user("DRI1111","37.7533", "-122.4056");
////            GeoFire geoFire = null;
//
//            geoFire.setLocation(mDRIVERID, new GeoLocation(Latitudes, Longotudes), new GeoFire.CompletionListener() {
//                @Override
//                public void onComplete(String key, DatabaseError error) {
//
//
//                    Log.e("geoFire.setLocation", mDRIVERID + " GeoFire  " + Latitudes + " <<<===>>> " + Longotudes);
//
//                    reference.child(mDRIVERID).child(mDRIVERIDlbl).setValue(mDRIVERID);
//                    reference.child(mDRIVERID).child(mLATITUDElbl).setValue(lats);
//                    reference.child(mDRIVERID).child(mLONGITUDElbl).setValue(longs);
//
////                    mCurrent = mMap.addMarker(new MarkerOptions().position(new LatLng(Latitudes, Longotudes))
////                            .title("You"));
////
////                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Latitudes, Longotudes), 12.0f));
//
//                }
//            });
//
//
////            Log.e("GeoFire",String.format("The location for key %s is [%f,%f]", Latitudes, Longotudes));
////            Log.e("GeoFire",String.format("The location for key %s is [%f,%f]", Latitudes, Longotudes));
//
//        }
//        Log.e("GeoFire", ("note get your loc"));
//    }





    @Override
    public void onConnected(@Nullable Bundle bundle) {
//        displayLocation();
        startLocationUpdates();

    }

    private void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, (com.google.android.gms.location.LocationListener) this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation=location;




//        setupLocation(geoFire);
////////

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((MapActivity) context, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, MY_PERMISSION_REQUEST_CODE);

        } else {
            if (CheckPlayService()) {

                buildGoogleApiClient();
                createLocationRequest();
//                displayLocation(geoFire);


                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                if (mLastLocation != null) {

                    final double Latitudes = mLastLocation.getLatitude();
                    final double Longotudes = mLastLocation.getLongitude();

                    final String lats,longs;

                    lats= String.valueOf(Latitudes);
                    longs= String.valueOf(Longotudes);

                }
                Log.e("GeoFire", ("note get your loc"));


            }

        }

    }


    private boolean CheckPlayService() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {

            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))

                GooglePlayServicesUtil.getErrorDialog(resultCode, (MapActivity)context, PLAY_SERVICE_RESOLUTION_REQUEST).show();
            else {

                Toast.makeText(this, "device not support", Toast.LENGTH_SHORT).show();

            }
            return false;
        }
        return true;
    }


}
