package com.abadou.utils;

public class Sensor {
    private final double rayon;

    public Sensor(double rayon) {
        this.rayon = rayon;
    }

    public boolean isInRange(double x1, double y1, double x2, double y2) {
        double dx = x1 - x2;
        double dy = y1 - y2;
        return Math.sqrt(dx * dx + dy * dy) <= rayon;
    }

    public double getRayon() {
        return rayon;
    }
}