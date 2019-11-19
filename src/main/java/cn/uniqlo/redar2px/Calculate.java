package com.uniqlo.radar2px;

import org.junit.Test;

public class Calculate {


    //雷达点
    private Point[] redarPoints = {
            Point.get(-33.6552, 22.9737),
            Point.get(-30.7968, 22.7348),
            Point.get(-28.4558, 22.6892),
            Point.get(-25.8418, 22.382),
            Point.get(-23.394, 22.8884),
            Point.get(-21.1434, 23.062),
            Point.get(-18.9523, 23.1542),
            Point.get(-16.497, 23.2234),
            Point.get(-14.2233, 23.2531),
            Point.get(-10.0103, 23.3862),
            Point.get(-7.78966, 23.247),
            Point.get(-10.9453, 7.99294),
            Point.get(-21.3688, 8.12865),
            Point.get(-27.7567, 8.09355),
            Point.get(-34.3748, 8.19794),
            Point.get(-13.4498, 8.04459),
            Point.get(-19.3612, 8.13824)
    };

    ////雷达点对应点图上像数点
    private Point[] pixelPoints = {
            Point.get(561, 790),
            Point.get(659, 798),
            Point.get(724, 799),
            Point.get(790, 809),
            Point.get(858, 797),
            Point.get(918, 795),
            Point.get(984, 790),
            Point.get(1046, 791),
            Point.get(1109, 791),
            Point.get(1239, 791),
            Point.get(1311, 790),
            Point.get(1432, 1248),
            Point.get(902, 1246),
            Point.get(728, 1247),
            Point.get(546, 1248),
            Point.get(1350, 1229),
            Point.get(995, 1224)
    };

    private final static double MIN_DIFF_X  = 5;//雷达坐标X轴差5以内到都不参与计算
    private final static double MIN_DIFF_Y = 1;//雷达坐标Y轴差1以内到都不参与计算


    @Test
    public void testRadar2PixelAverage() {

        double radarX = -7.01705;
        double radarY = 8.96362;

        //Point curRedar = Point.get(radarX, radarY);

        double px = getPixelX(radarX);
        double py = getPixelY(radarY);

        System.out.println("(" + radarX + "," + radarY + ")->" + "(" + px + "," + py + ")");

    }

    //雷达图Y轴映射到展示图片上X轴像数点
    private double getPixelX(double redarX) {
        double A = 0;
        double B = 0;
        int count = 0;

        for (int i = 0; i < redarPoints.length; i++) {
            Point p1 = redarPoints[i];
            Point px1 = pixelPoints[i];
            for (int j = i + 1; j < redarPoints.length; j++) {
                Point p2 = redarPoints[j];
                if (p1.getX() - p2.getX() < MIN_DIFF_X) continue;//雷达坐标X轴差5以内到都不参与计算

                Point px2 = pixelPoints[j];

                Mapping mappingX1 = Mapping.get(p1.getX(), px1.getX());
                Mapping mappingX2 = Mapping.get(p2.getX(), px2.getX());
                double a = getA(mappingX1, mappingX2);
                double b = getB(mappingX1, mappingX2);

                A += a;
                B += b;
                count++;
            }
        }

        if (count > 0) {
            A = A / count;
            B = B / count;
        }

        double px = A * redarX + B;

        return px;
    }

    //雷达图Y轴映射到展示图片上Y轴像数点
    private double getPixelY(double redarY) {

        double C = 0;
        double D = 0;
        int count = 0;

        for (int i = 0; i < redarPoints.length; i++) {
            Point p1 = redarPoints[i];
            Point px1 = pixelPoints[i];
            for (int j = i + 1; j < redarPoints.length; j++) {
                Point p2 = redarPoints[j];
                if (p1.getY() - p2.getY() < MIN_DIFF_Y) continue;//雷达坐标Y轴差1以内到都不参与计算

                Point px2 = pixelPoints[j];

                Mapping mappingY1 = Mapping.get(p1.getY(), px1.getY());
                Mapping mappingY2 = Mapping.get(p2.getY(), px2.getY());
                double c = getA(mappingY1, mappingY2);
                double d = getB(mappingY1, mappingY2);

                C += c;
                D += d;
                count++;
            }
        }

        if (count > 0) {
            C = C / count;
            D = D / count;
        }

        double py = C * redarY + D;

        return py;
    }


    //计算横轴系数a*x+b= px中的a
    private double getA(Mapping p1, Mapping p2) {
        double a = (p2.getPixel() - p1.getPixel()) / (p2.getRedar() - p1.getRedar());
        return a;
    }

    //计算横轴系数a*x+b= px中的b
    private double getB(Mapping p1, Mapping p2) {
        double b = (p1.getPixel() * p2.getRedar() - p2.getPixel() * p1.getRedar()) / (p2.getRedar() - p1.getRedar());
        return b;
    }


}
