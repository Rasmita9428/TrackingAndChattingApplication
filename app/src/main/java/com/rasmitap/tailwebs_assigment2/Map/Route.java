package com.rasmitap.tailwebs_assigment2.Map;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class Route {
    private String copyright;
    private String country;
    private String distanceText;
    private String durationText;
    private String endAddressText;
    private int length;
    private String name;
    private final List<LatLng> points = new ArrayList();
    private String polyline;
    private List<Segment> segments = new ArrayList();
    private String warning;

    public String getEndAddressText() {
        return this.endAddressText;
    }

    public void setEndAddressText(String endAddressText) {
        this.endAddressText = endAddressText;
    }

    public String getDurationText() {
        return this.durationText;
    }

    public void setDurationText(String durationText) {
        this.durationText = durationText;
    }

    public String getDistanceText() {
        return this.distanceText;
    }

    public void setDistanceText(String distanceText) {
        this.distanceText = distanceText;
    }

    public void setSegments(List<Segment> segments) {
        this.segments = segments;
    }

    public void addPoint(LatLng p) {
        this.points.add(p);
    }

    public void addPoints(List<LatLng> points) {
        this.points.addAll(points);
    }

    public List<LatLng> getPoints() {
        return this.points;
    }

    public void addSegment(Segment s) {
        this.segments.add(s);
    }

    public List<Segment> getSegments() {
        return this.segments;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public String getCopyright() {
        return this.copyright;
    }

    public void setWarning(String warning) {
        this.warning = warning;
    }

    public String getWarning() {
        return this.warning;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountry() {
        return this.country;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getLength() {
        return this.length;
    }

    public void setPolyline(String polyline) {
        this.polyline = polyline;
    }

    public String getPolyline() {
        return this.polyline;
    }
}
