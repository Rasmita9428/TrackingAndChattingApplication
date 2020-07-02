package com.rasmitap.tailwebs_assigment2.Map;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.BuildConfig;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class GoogleParser extends XMLParser implements Parser {
    private int distance;

    public GoogleParser(String feedUrl) {
        super(feedUrl);
    }

    public Route parse() {
        String result = convertStreamToString(getInputStream());
        if (result == null) {
            return null;
        }
        Route route = new Route();
        Segment segment = new Segment();
        try {
            JSONObject json = new JSONObject(result);
            route.setPolyline(json.getJSONArray("routes").getJSONObject(0).getJSONObject("overview_polyline").getString("points"));
            JSONObject jsonRoute = json.getJSONArray("routes").getJSONObject(0);
            JSONObject leg = jsonRoute.getJSONArray("legs").getJSONObject(0);
            JSONArray steps = leg.getJSONArray("steps");
            int numSteps = steps.length();
            route.setName(leg.getString("start_address") + " to " + leg.getString("end_address"));
            route.setCopyright(jsonRoute.getString("copyrights"));
            route.setDurationText(leg.getJSONObject("duration").getString("text"));
            route.setDistanceText(leg.getJSONObject("distance").getString("text"));
            route.setEndAddressText(leg.getString("end_address"));
            route.setLength(leg.getJSONObject("distance").getInt(FirebaseAnalytics.Param.VALUE));
            if (!jsonRoute.getJSONArray("warnings").isNull(0)) {
                route.setWarning(jsonRoute.getJSONArray("warnings").getString(0));
            }
            for (int i = 0; i < numSteps; i++) {
                JSONObject step = steps.getJSONObject(i);
                JSONObject start = step.getJSONObject("start_location");
                segment.setPoint(new LatLng(start.getDouble("lat"), start.getDouble("lng")));
                int length = step.getJSONObject("distance").getInt(FirebaseAnalytics.Param.VALUE);
                this.distance += length;
                segment.setLength(length);
                segment.setDistance((double) (this.distance / 1000));
                segment.setInstruction(step.getString("html_instructions"));
                if (step.has("maneuver")) {
                    segment.setManeuver(step.getString("maneuver"));
                } else {
                    segment.setManeuver(BuildConfig.FLAVOR);
                }
                route.addPoints(decodePolyLine(step.getJSONObject("polyline").getString("points")));
                route.addSegment(segment.copy());
            }
            return route;
        } catch (JSONException e) {
            Log.e("Routing Error", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
  /*  private static String convertStreamToString(java.io.InputStream r7) {
        *//*
        if (r7 != 0) goto L_0x0004;
    L_0x0002:
        r4 = 0;
    L_0x0003:
        return r4;
    L_0x0004:
        r2 = new java.io.BufferedReader;
        r4 = new java.io.InputStreamReader;
        r4.<init>(r7);
        r2.<init>(r4);
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r1 = 0;
    L_0x0014:
        r1 = r2.readLine();	 Catch:{ IOException -> 0x001e }
        if (r1 == 0) goto L_0x0030;
    L_0x001a:
        r3.append(r1);	 Catch:{ IOException -> 0x001e }
        goto L_0x0014;
    L_0x001e:
        r0 = move-exception;
        r4 = "Routing Error";
        r5 = r0.getMessage();	 Catch:{ all -> 0x004a }
        android.util.Log.e(r4, r5);	 Catch:{ all -> 0x004a }
        r7.close();	 Catch:{ IOException -> 0x003f }
    L_0x002b:
        r4 = r3.toString();
        goto L_0x0003;
    L_0x0030:
        r7.close();	 Catch:{ IOException -> 0x0034 }
        goto L_0x002b;
    L_0x0034:
        r0 = move-exception;
        r4 = "Routing Error";
        r5 = r0.getMessage();
        android.util.Log.e(r4, r5);
        goto L_0x002b;
    L_0x003f:
        r0 = move-exception;
        r4 = "Routing Error";
        r5 = r0.getMessage();
        android.util.Log.e(r4, r5);
        goto L_0x002b;
    L_0x004a:
        r4 = move-exception;
        r7.close();	 Catch:{ IOException -> 0x004f }
    L_0x004e:
        throw r4;
    L_0x004f:
        r0 = move-exception;
        r5 = "Routing Error";
        r6 = r0.getMessage();
        android.util.Log.e(r5, r6);
        goto L_0x004e;
        *//*
        throw new UnsupportedOperationException("Method not decompiled: com.karwa.app.hyperlinkGoogle.route.GoogleParser.convertStreamToString(java.io.InputStream):java.lang.String");
    }*/
    private String convertStreamToString(InputStream is) {
        ByteArrayOutputStream oas = new ByteArrayOutputStream();
        copyStream(is, oas);
        String t = oas.toString();
        try {
            oas.close();
            oas = null;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return t;
    }
    private void copyStream(InputStream is, OutputStream os)
    {
        final int buffer_size = 1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
                int count=is.read(bytes, 0, buffer_size);
                if(count==-1)
                    break;
                os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }

    private List<LatLng> decodePolyLine(String poly) {
        int len = poly.length();
        int index = 0;
        List<LatLng> decoded = new ArrayList();
        int lat = 0;
        int lng = 0;
        while (index < len) {
            int index2;
            int shift = 0;
            int result = 0;
            while (true) {
                index2 = index + 1;
                int b = poly.charAt(index) - 63;
                result |= (b & 31) << shift;
                shift += 5;
                if (b < 32) {
                    break;
                }
                index = index2;
            }
            lat += (result & 1) != 0 ? (result >> 1) ^ -1 : result >> 1;
            shift = 0;
            result = 0;
            index = index2;
            while (true) {
                index2 = index + 1;
                int b = poly.charAt(index) - 63;
                result |= (b & 31) << shift;
                shift += 5;
                if (b < 32) {
                    break;
                }
                index = index2;
            }
            lng += (result & 1) != 0 ? (result >> 1) ^ -1 : result >> 1;
            decoded.add(new LatLng(((double) lat) / 100000.0d, ((double) lng) / 100000.0d));
            index = index2;
        }
        return decoded;
    }
}
