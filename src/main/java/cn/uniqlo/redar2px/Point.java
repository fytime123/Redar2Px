package com.uniqlo.robot;

public class Point {

    private double x;
    private double y;

    private double px;
    private double py;

    public static Point get(double x, double y,double px, double py) {
        Point point = new Point();

        point.setX(x);
        point.setY(y);
        point.setPx(px);
        point.setPy(py);

        return point;
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

    public double getPx() {
        return px;
    }

    public void setPx(double px) {
        this.px = px;
    }

    public double getPy() {
        return py;
    }

    public void setPy(double py) {
        this.py = py;
    }
}
