package com.example.mi_zan.model;

public class Center {
    private double latitude;
    private double longitude;

    public Center(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters (dan Setters jika perlu)
    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}