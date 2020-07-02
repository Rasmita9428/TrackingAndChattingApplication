package com.rasmitap.tailwebs_assigment2.Map;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Iterator;


public class Routing extends AsyncTask<LatLng, Void, Route> {
    protected ArrayList<RoutingListener> _aListeners = new ArrayList();
    protected TravelMode _mTravelMode;

    public enum TravelMode {
        BIKING("biking"),
        DRIVING("driving"),
        WALKING("walking"),
        TRANSIT("transit");
        
        protected String _sValue;

        private TravelMode(String sValue) {
            this._sValue = sValue;
        }

        protected String getValue() {
            return this._sValue;
        }
    }

    public Routing(TravelMode mTravelMode) {
        this._mTravelMode = mTravelMode;
    }

    public void registerListener(RoutingListener mListener) {
        this._aListeners.add(mListener);
    }

    protected void dispatchOnStart() {
        Iterator it = this._aListeners.iterator();
        while (it.hasNext()) {
            ((RoutingListener) it.next()).onRoutingStart();
        }
    }

    protected void dispatchOnFailure() {
        Iterator it = this._aListeners.iterator();
        while (it.hasNext()) {
            ((RoutingListener) it.next()).onRoutingFailure();
        }
    }

    protected void dispatchOnSuccess(PolylineOptions mOptions, Route route) {
        Iterator it = this._aListeners.iterator();
        while (it.hasNext()) {
            ((RoutingListener) it.next()).onRoutingSuccess(mOptions, route);
        }
    }

    protected Route doInBackground(LatLng... aPoints) {
        for (LatLng mPoint : aPoints) {
            if (mPoint == null) {
                return null;
            }
        }
        return new GoogleParser(constructURL(aPoints)).parse();
    }

    protected String constructURL(LatLng... points) {
        LatLng start = points[0];
        LatLng dest = points[1];
        StringBuffer mBuf = new StringBuffer("http://maps.googleapis.com/maps/api/directions/json?");
        mBuf.append("origin=");
        mBuf.append(start.latitude);
        mBuf.append(',');
        mBuf.append(start.longitude);
        mBuf.append("&destination=");
        mBuf.append(dest.latitude);
        mBuf.append(',');
        mBuf.append(dest.longitude);
        mBuf.append("&sensor=true&mode=");
        mBuf.append(this._mTravelMode.getValue());
        return mBuf.toString();
    }

    protected void onPreExecute() {
        dispatchOnStart();
    }

    protected void onPostExecute(Route result) {
        if (result == null) {
            dispatchOnFailure();
            return;
        }
        PolylineOptions mOptions = new PolylineOptions();
        for (LatLng point : result.getPoints()) {
            mOptions.add(point);
        }
        dispatchOnSuccess(mOptions, result);
    }
}
