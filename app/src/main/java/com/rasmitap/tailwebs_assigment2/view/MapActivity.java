package com.rasmitap.tailwebs_assigment2.view;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;

import com.google.android.gms.common.util.MapUtils;
import com.google.android.gms.location.LocationListener;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.FirebaseApp;
import com.rasmitap.tailwebs_assigment2.Map.Route;
import com.rasmitap.tailwebs_assigment2.Map.Routing;
import com.rasmitap.tailwebs_assigment2.Map.RoutingListener;
import com.rasmitap.tailwebs_assigment2.R;
import com.rasmitap.tailwebs_assigment2.utils.ConstantStore;
import com.rasmitap.tailwebs_assigment2.utils.GPSTracker;
import com.rasmitap.tailwebs_assigment2.utils.GlobalMethods;
import com.rasmitap.tailwebs_assigment2.utils.Utility;
import com.rasmitap.tailwebs_assigment2.utils.Validation;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MapActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnCameraIdleListener, RoutingListener,GoogleMap.OnMarkerClickListener,GoogleMap.InfoWindowAdapter,
        LocationListener  {

    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    private BottomSheetBehavior mBottomSheetBehavior;
    View bottomSheet;
    TextView txttimer, btn_stoptrack;
    private int seconds = 0;
    private boolean running;
    private boolean wasRunning;
    GPSTracker gps;
    MapUtils mapUtil;
    String StartLat = "";
    String StartLon = "";
    LatLng start_location_lanlng, destination_location_lanlng;
    Polyline line;
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String API_KEY = "AIzaSyCfhpyqlZsI6hoqDjdN42J60FNqLSv1Te0";
    private Polyline polyline;
    private static final int UPDATE_INTERVAL = 15 * 1000;
    private static final int FASTEST_UPDATE_INTERVAL = 2 * 1000;

    public static String LATELONG = "latlong";
    public static String LONG = "long";
    public static String ADDRESS = "address";
    Marker pick_up_location_marker, destination_location_marker;
    boolean is_camera_move = false;
    // Static LatLng
    LatLng startLatLng;
    LatLng endLatLng;
    LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        txttimer = (TextView) findViewById(R.id.txttimer);
        btn_stoptrack = (TextView) findViewById(R.id.btn_stoptrack);
        mapUtil = new MapUtils();
        FirebaseApp.initializeApp(this);
        getLocationLatLong();
        locationRequest = LocationRequest.create()
                //Set the required accuracy level
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                //Set the desired (inexact) frequency of location updates
                .setInterval(UPDATE_INTERVAL)
                //Throttle the max rate of update requests
                .setFastestInterval(FASTEST_UPDATE_INTERVAL);
        if (savedInstanceState != null) {

            // Get the previous state of the stopwatch
            // if the activity has been
            // destroyed and recreated.
            seconds
                    = savedInstanceState
                    .getInt("seconds");
            running
                    = savedInstanceState
                    .getBoolean("running");
            wasRunning
                    = savedInstanceState
                    .getBoolean("wasRunning");
        }
        runTimer();
        onClickStart();

        btn_stoptrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickStop();
            }
        });
        if (gps != null) {
            if (gps.canGetLocation()) {
                getLocationLatLong();
            } else {
                gps.showSettingsAlert();
            }
        }

        checkPermission();
        String urlTopass = makeURL(startLatLng.latitude,
                startLatLng.longitude, endLatLng.latitude,
                endLatLng.longitude);
        new connectAsyncTask(urlTopass).execute();
//        mGoogleMap.setMyLocationEnabled(true);
//        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(startLatLng));
//        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(12));

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
    }

    private class connectAsyncTask extends AsyncTask<Void, Void, String> {
        private ProgressDialog progressDialog;
        String url;

        connectAsyncTask(String urlPass) {
            url = urlPass;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progressDialog = new ProgressDialog(MapActivity.this);
            progressDialog.setMessage("Fetching route, Please wait...");
            progressDialog.setIndeterminate(true);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            JSONParser jParser = new JSONParser();
            String json = jParser.getJSONFromUrl(url);
            return json;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.hide();
            if (result != null) {
                drawPath(result);
            }
        }
    }

    public String makeURL(double sourcelat, double sourcelog, double destlat,
                          double destlog) {
        StringBuilder urlString = new StringBuilder();
        urlString.append("http://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");// from
        urlString.append(Double.toString(sourcelat));
        urlString.append(",");
        urlString.append(Double.toString(sourcelog));
        urlString.append("&destination=");// to
        urlString.append(Double.toString(destlat));
        urlString.append(",");
        urlString.append(Double.toString(destlog));
        urlString.append("&sensor=false&mode=driving&alternatives=true");
        return urlString.toString();
    }

    public class JSONParser {

        InputStream is = null;
        JSONObject jObj = null;
        String json = "";

        // constructor
        public JSONParser() {
        }

        public String getJSONFromUrl(String url) {

            // Making HTTP request
            try {
                // defaultHttpClient
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);

                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }

                json = sb.toString();
                is.close();
            } catch (Exception e) {
                Log.e("Buffer Error", "Error converting result " + e.toString());
            }
            return json;

        }
    }

    public void drawPath(String result) {
        if (line != null) {
            mGoogleMap.clear();
        }
        mGoogleMap.addMarker(new MarkerOptions().position(endLatLng).icon(
                BitmapDescriptorFactory.fromResource(R.drawable.marker)));
        mGoogleMap.addMarker(new MarkerOptions().position(startLatLng).icon(
                BitmapDescriptorFactory.fromResource(R.drawable.marker)));
        try {
            // Tranform the string into a json object
            final JSONObject json = new JSONObject(result);
            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolylines = routes
                    .getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
            List<LatLng> list = decodePoly(encodedString);

            for (int z = 0; z < list.size() - 1; z++) {
                LatLng src = list.get(z);
                LatLng dest = list.get(z + 1);
                line = mGoogleMap.addPolyline(new PolylineOptions()
                        .add(new LatLng(src.latitude, src.longitude),
                                new LatLng(dest.latitude, dest.longitude))
                        .width(5).color(Color.BLUE).geodesic(true));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    private void initMapConponet() {

        Log.e("test", "==>>initMapConponet() calll");

        try {
            mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            MapsInitializer.initialize(getApplicationContext());
            mapFrag.getMapAsync(this);
            Log.e("test", "camera change");
            //   mapFragment.getMapAsync(HomeFragment.this);

            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setPowerRequirement(Criteria.POWER_HIGH);
            criteria.setAltitudeRequired(false);
            criteria.setSpeedRequired(false);
            criteria.setCostAllowed(true);
            criteria.setBearingRequired(false);

//API level 9 and up
            criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
            criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION};
            if (GlobalMethods.isPermissionNotGranted(getApplicationContext(), permissions)) {
                requestPermissions(permissions, ConstantStore.PERMISSION_CODE);
                return;
            } else {
                initMapConponet();
            }
        } else
            initMapConponet();
    }

    public void getLocationLatLong() {
        gps = new GPSTracker(this);
        if (gps != null) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            String lata = String.valueOf(latitude);
            String longt = String.valueOf(longitude);
            ConstantStore.LATITUDE = latitude;
            ConstantStore.LONGITUDE = longitude;
            if (!lata.equalsIgnoreCase("0.0") && !longt.equalsIgnoreCase("0.0")) {
                startLatLng = new LatLng(latitude, longitude);
                endLatLng = new LatLng(latitude, longitude);
            }
        }
    }

    @Override
    public void onSaveInstanceState(
            Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState
                .putInt("seconds", seconds);
        savedInstanceState
                .putBoolean("running", running);
        savedInstanceState
                .putBoolean("wasRunning", wasRunning);
    }

    @Override
    public void onPause() {
        super.onPause();
        wasRunning = running;
        running = false;
        //stop location updates when Activity is no longer active
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (wasRunning) {
            running = true;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mGoogleMap.getUiSettings().isMyLocationButtonEnabled();
        mGoogleMap.getUiSettings().isZoomControlsEnabled();
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
        // mMap.setOnInfoWindowClickListener(this);

        fixZoomForMarkers1(mGoogleMap, startLatLng);


        LatLng latLng = new LatLng(ConstantStore.LATITUDE, ConstantStore.LONGITUDE);
        Log.e("test", "onMapReady: " + ConstantStore.LATITUDE);
        Log.e("test", "onMapReady: " + ConstantStore.LONGITUDE);

       /* mMap.addMarker(new MarkerOptions().position(latLng)
                .title(CommonKeys.CUSRRENT_LOCATION));*/
        destination_location_lanlng = latLng;
        fixZoomForMarkers(mGoogleMap, latLng);

    }

    public void fixZoomForMarkers(GoogleMap googleMap, LatLng latLng) {

        if (destination_location_marker != null) {
            destination_location_marker.remove();
        }
        destination_location_marker = mGoogleMap.addMarker(new MarkerOptions().position(destination_location_lanlng).title("Current Point").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
        LatLngBounds.Builder bc = new LatLngBounds.Builder();
        bc.include(destination_location_marker.getPosition());
        CameraPosition cameraPosition = CameraPosition.fromLatLngZoom(latLng, 15.0f);
        CameraUpdate cu = CameraUpdateFactory.newCameraPosition(cameraPosition);
        googleMap.animateCamera(cu);
        destination_location_marker.showInfoWindow();


        // googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), 20),2000,null);
    }

    public void fixZoomForMarkers1(GoogleMap googleMap, LatLng latLng) {

        if (start_location_lanlng != null) {
            pick_up_location_marker = mGoogleMap.addMarker(new MarkerOptions().position(start_location_lanlng).title("Started Point").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
            LatLngBounds.Builder bc = new LatLngBounds.Builder();
            bc.include(pick_up_location_marker.getPosition());
            CameraPosition cameraPosition = CameraPosition.fromLatLngZoom(latLng, 15.0f);
            CameraUpdate cu = CameraUpdateFactory.newCameraPosition(cameraPosition);
            googleMap.animateCamera(cu);
            pick_up_location_marker.showInfoWindow();
        }

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        destination_location_lanlng = new LatLng(location.getLatitude(), location.getLongitude());
        mCurrLocationMarker = mGoogleMap.addMarker(new MarkerOptions().position(destination_location_lanlng).title("Current Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
        LatLngBounds.Builder bc = new LatLngBounds.Builder();
        bc.include(mCurrLocationMarker.getPosition());
        CameraPosition cameraPosition = CameraPosition.fromLatLngZoom(destination_location_lanlng, 15.0f);
        CameraUpdate cu = CameraUpdateFactory.newCameraPosition(cameraPosition);
        mGoogleMap.animateCamera(cu);
        mCurrLocationMarker.showInfoWindow();
        UpdateRoute1(start_location_lanlng, destination_location_lanlng);
        }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new android.app.AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MapActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    LatLngBounds.Builder builder;

    private void UpdateRoute1(LatLng start, LatLng destination) {

        if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            // mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        if (start != null && destination != null) {
            this.builder = new LatLngBounds.Builder();
            this.builder.include(start);
            this.builder.include(destination);
            Routing routing = new Routing(Routing.TravelMode.DRIVING);
            routing.registerListener((RoutingListener) MapActivity.this);
            routing.execute(new LatLng[]{start, destination});
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            mGoogleMap.setMyLocationEnabled(true);
                        } else {
                            // Show rationale and request permission.
                        }
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void onClickStart() {
        running = true;
    }

    public void onClickStop() {
        running = false;
    }

    public void onClickReset(View view) {
        running = false;
        seconds = 0;
    }

    private void runTimer() {

        // Get the text view.
        final TextView timeView
                = (TextView) findViewById(
                R.id.txttimer);
        final Handler handler
                = new Handler();
        handler.post(new Runnable() {
            @Override

            public void run() {
                int hours = seconds / 3600;
                int minutes = (seconds % 3600) / 60;
                int secs = seconds % 60;

                // Format the seconds into hours, minutes,
                // and seconds.
                String time
                        = String
                        .format(Locale.CANADA.getDefault(),
                                "%d:%02d:%02d", hours,
                                minutes, secs);

                // Set the text view text.
                timeView.setText(time);

                // If running is true, increment the
                // seconds variable.
                if (running) {
                    seconds++;
                }

                // Post the code again
                // with a delay of 1 second.
                handler.postDelayed(this, 1000);
            }
        });
    }



    @Override
    public View getInfoWindow(Marker marker) {
        View view = getLayoutInflater().inflate(R.layout.info_window,null);
        TextView tv_km=(TextView) view.findViewById(R.id.tv_distance);
        if(Validation.isRequiredField(marker.getTitle()))
        {
            tv_km.setText(marker.getTitle());
        }
        return view;

    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public void onCameraIdle() {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onRoutingFailure() {

    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(PolylineOptions mPolyOptions, Route route) {
        Log.e("test","onRoutingSuccess");
        if (this.mGoogleMap != null) {
            mGoogleMap.clear();
            ///this.route1 = route;
            //this.IsRoutSucess = 0;
            String time = route.getDurationText().replace(" ", "\n");
            String timeNew = route.getDurationText().toString();
            if(start_location_lanlng!=null){
                pick_up_location_marker=  mGoogleMap.addMarker(new MarkerOptions().position(start_location_lanlng).title("SET_PICKUP_LOCATION").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
                // pick_up_location_marker.hideInfoWindow();
            }
            if(destination_location_lanlng!=null){
                destination_location_marker=  mGoogleMap.addMarker(new MarkerOptions().position(destination_location_lanlng).title("SET_DESTINATION").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
                destination_location_marker.showInfoWindow();
            }

            try {
                this.mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(this.builder.build(), (int) getResources().getDimension(R.dimen._50sdp)));
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        is_camera_move=true;
        //Check the current state of bottom sheet
        bottomSheet.setVisibility(View.VISIBLE);

        if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED){
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            Log.e("test","STATE_EXPANDED");
        }
        else{
            Log.e("test","STATE_COLLAPSED");

            //else if state is expanded collapse it
            // mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        PolylineOptions polyoptions = new PolylineOptions();

        if(this.polyline!=null){
            this.polyline.remove();
        }
        polyoptions.color(Color.parseColor("#003366"));
        polyoptions.width(8.0f);
        polyoptions.addAll(mPolyOptions.getPoints());
        this.polyline = this.mGoogleMap.addPolyline(polyoptions);
        try {
            this.mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(this.builder.build(), (int) getResources().getDimension(R.dimen._50sdp)));
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }
}