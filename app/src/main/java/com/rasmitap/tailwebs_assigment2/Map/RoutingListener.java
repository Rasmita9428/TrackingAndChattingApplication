package com.rasmitap.tailwebs_assigment2.Map;

import com.google.android.gms.maps.model.PolylineOptions;


public interface RoutingListener {
    void onRoutingFailure();

    void onRoutingStart();

    void onRoutingSuccess(PolylineOptions polylineOptions, Route route);
}
