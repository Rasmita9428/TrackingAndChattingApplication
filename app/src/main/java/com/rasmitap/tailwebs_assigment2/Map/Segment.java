package com.rasmitap.tailwebs_assigment2.Map;

import com.google.android.gms.maps.model.LatLng;

public class Segment {
    private double distance;
    private String instruction;
    private int length;
    private String maneuver;
    private LatLng start;

    public void setInstruction(String turn) {
        this.instruction = turn;
    }

    public String getInstruction() {
        return this.instruction;
    }

    public void setManeuver(String mm) {
        this.maneuver = mm;
    }

    public String getManeuver() {
        return this.maneuver;
    }

    public void setPoint(LatLng point) {
        this.start = point;
    }

    public LatLng startPoint() {
        return this.start;
    }

    public Segment copy() {
        Segment copy = new Segment();
        copy.start = this.start;
        copy.instruction = this.instruction;
        copy.maneuver = this.maneuver;
        copy.length = this.length;
        copy.distance = this.distance;
        return copy;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getLength() {
        return this.length;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getDistance() {
        return this.distance;
    }
}
