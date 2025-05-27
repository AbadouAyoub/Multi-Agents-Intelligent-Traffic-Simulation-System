package com.abadou.utils;

import java.io.Serializable;

public class Position implements Serializable {
    private double x;
    private double y;

    public Position() {
        this(0, 0);
    }

    public Position(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double distanceTo(Position other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}