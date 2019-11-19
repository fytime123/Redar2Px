package com.uniqlo.radar2px;

public class Point {

    private double x;
    private double y;

    public static Point get(double x, double y) {
        return new Point(x, y);
    }

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}
