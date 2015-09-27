package com.cdvdev.atmsearcher.models;

import java.io.Serializable;

public class LocationPoint implements Serializable {
    private double latitude;
    private double longitude;
    private double altitude;

    public LocationPoint(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = 0; //height always for us = 0
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getAltitude() {
        return altitude;
    }
}
