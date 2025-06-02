package com.example.mi_zan.model;

public class LocationRestriction {
    private Circle circle;

    public LocationRestriction(Circle circle) {
        this.circle = circle;
    }

    public Circle getCircle() {
        return circle;
    }

    public void setCircle(Circle circle) {
        this.circle = circle;
    }
}