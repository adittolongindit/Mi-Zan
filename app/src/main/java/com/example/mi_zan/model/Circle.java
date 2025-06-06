package com.example.mi_zan.model;

public class Circle {
    private Center center;
    private double radius;

    public Circle(Center center, double radius) {
        this.center = center;
        this.radius = radius;
    }

    // Getters (dan Setters jika perlu)
    public Center getCenter() {
        return center;
    }

    public double getRadius() {
        return radius;
    }
}