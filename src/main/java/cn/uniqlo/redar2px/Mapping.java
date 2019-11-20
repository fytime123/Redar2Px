package com.uniqlo.robot;

public class Mapping {
    private double redar;
    private double pixel;

    public static Mapping get(double redar, double pixel){
        return new Mapping(redar,pixel);
    }

    public Mapping(double redar, double pixel) {
        this.redar = redar;
        this.pixel = pixel;
    }

    public double getRedar() {
        return redar;
    }

    public void setRedar(double redar) {
        this.redar = redar;
    }

    public double getPixel() {
        return pixel;
    }

    public void setPixel(double pixel) {
        this.pixel = pixel;
    }
}
