package com.uniqlo.robot;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Calculate {

    //展示图为文件夹中的mall.jpg

    //雷达点+雷达点对应点图上像数点
    private Point[] radarPoints = {
            Point.get(-33.6552, 22.9737, 561, 790),
            Point.get(-30.7968, 22.7348, 659, 798),
            Point.get(-28.4558, 22.6892, 724, 799),
            Point.get(-25.8418, 22.382, 790, 809),
            Point.get(-23.394, 22.8884, 858, 797),
            Point.get(-21.1434, 23.062, 918, 795),
            Point.get(-18.9523, 23.1542, 984, 790),
            Point.get(-16.497, 23.2234, 1046, 791),
            Point.get(-14.2233, 23.2531, 1109, 791),
            Point.get(-10.0103, 23.3862, 1239, 791),
            Point.get(-7.78966, 23.247, 1311, 790),
            Point.get(-10.9453, 7.99294, 1432, 1248),
            Point.get(-21.3688, 8.12865, 902, 1246),
            Point.get(-27.7567, 8.09355, 728, 1247),
            Point.get(-34.3748, 8.19794, 546, 1248),
            Point.get(-13.4498, 8.04459, 1350, 1229),
            Point.get(-19.3612, 8.13824, 995, 1224)
    };

    private final static double MIN_DIFF_X = 5;//雷达坐标X轴差5以内到都不参与计算
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

        for (int i = 0; i < radarPoints.length; i++) {
            Point p1 = radarPoints[i];
            for (int j = i + 1; j < radarPoints.length; j++) {
                Point p2 = radarPoints[j];
                if (p1.getX() - p2.getX() < MIN_DIFF_X) continue;//雷达坐标X轴差5以内到都不参与计算

                Mapping mappingX1 = Mapping.get(p1.getX(), p1.getPx());
                Mapping mappingX2 = Mapping.get(p2.getX(), p2.getPx());

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

        for (int i = 0; i < radarPoints.length; i++) {
            Point p1 = radarPoints[i];
            for (int j = i + 1; j < radarPoints.length; j++) {
                Point p2 = radarPoints[j];
                if (p1.getY() - p2.getY() < MIN_DIFF_Y) continue;//雷达坐标Y轴差1以内到都不参与计算


                Mapping mappingY1 = Mapping.get(p1.getY(), p1.getPy());
                Mapping mappingY2 = Mapping.get(p2.getY(), p2.getPy());
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












    //附近的4分子之1的数据计算值；附近一半数据计算值；整个数据计算值
    private final static float[] priority = {1.0f / 4.0f, 1.0f / 2.0f, 1.0f};//最近数据比例
    private final static float[] weight = {5, 2, 1};//数据权重


    public void testRadar2PixelSortWeight() {

        double radarX = -7.01705;
        double radarY = 8.96362;

        List<Range> ranges = scanDistance(radarX, radarY);
        int count = ranges.size();

        //权重总值
        double total = 0;
        for (int i = 0; i < weight.length; i++) total += weight[i];

        //根据最近的点计算
        List<Point> points = new ArrayList<>();
        for (int i = 0; i < priority.length; i++) {
            int lastCount = (int) (priority[i] * count);
            Point point = new Point();
            point.setX(radarX);
            point.setY(radarY);
            double px = getPixelX(radarX, ranges, lastCount);
            double py = getPixelY(radarY, ranges, lastCount);

            //根据权重修正
            px = px * weight[i]/total;
            py = py * weight[i]/total;

            point.setPx(px);
            point.setPy(py);

            points.add(point);
        }

        double px =0;
        double py = 0;
        for(Point p:points){
            px += p.getPx();
            py += p.getPy();
        }

        System.out.println("(" + radarX + "," + radarY + ")->" + "(" + px + "," + py + ")");
    }


    private double distance(Point p1, Point p2) {

        double d = (p1.getX() - p2.getX()) * (p1.getX() - p2.getX()) + (p1.getY() - p2.getY()) * (p1.getY() - p2.getY());
        d = Math.sqrt(d);

        return d;
    }

    private List<Range> scanDistance(double radarX, double radarY) {
        List<Range> ranges = new ArrayList<>();

        Point point = new Point();
        point.setX(radarX);
        point.setY(radarY);

        for (Point p : radarPoints) {
            Range r = new Range();
            r.setP(p);
            double d = distance(p, point);
            r.setDistance(d);
            ranges.add(r);
        }

        Collections.sort(ranges, new Comparator<Range>() {
            @Override
            public int compare(Range o1, Range o2) {
                int sort = 0;
                double d1 = o1.getDistance();
                double d2 = o2.getDistance();
                if (d1 > d2) {
                    sort = 1;
                } else if (d1 < d2) {
                    sort = -1;
                }
                return sort;
            }
        });

        return ranges;
    }

    private double getPixelX(double radarX, List<Range> ranges, int lastCount) {

        double ratioA = 0;
        double ratioB = 0;
        int count = 0;

        for (int i = 0; i < lastCount; i++) {

            Range range1 = ranges.get(i);
            Point p1 = range1.getP();

            for (int j = i + 1; j < lastCount; j++) {
                Range range2 = ranges.get(j);
                Point p2 = range2.getP();

                if (p1.getX() - p2.getX() < MIN_DIFF_X) continue;//雷达坐标X轴差5以内到都不参与计算

                Mapping mappingX1 = Mapping.get(p1.getX(), p1.getPx());
                Mapping mappingX2 = Mapping.get(p2.getX(), p2.getPx());

                double a = getA(mappingX1, mappingX2);
                double b = getB(mappingX1, mappingX2);

                ratioA += a;
                ratioB += b;
                count++;
            }
        }

        if (count > 0) {
            ratioA = ratioA / count;
            ratioB = ratioB / count;
        }

        double px = ratioA * radarX + ratioB;

        return px;
    }

    private double getPixelY(double radarY, List<Range> ranges, int lastCount) {

        double ratioA = 0;
        double ratioB = 0;
        int count = 0;

        for (int i = 0; i < lastCount; i++) {

            Range range1 = ranges.get(i);
            Point p1 = range1.getP();

            for (int j = i + 1; j < lastCount; j++) {
                Range range2 = ranges.get(j);
                Point p2 = range2.getP();

                if (p1.getY() - p2.getY() < MIN_DIFF_Y) continue;//雷达坐标Y轴差1以内到都不参与计算

                Mapping mappingY1 = Mapping.get(p1.getY(), p1.getPy());
                Mapping mappingY2 = Mapping.get(p2.getY(), p2.getPy());

                double a = getA(mappingY1, mappingY2);
                double b = getB(mappingY1, mappingY2);

                ratioA += a;
                ratioB += b;
                count++;
            }
        }

        if (count > 0) {
            ratioA = ratioA / count;
            ratioB = ratioB / count;
        }

        double px = ratioA * radarY + ratioB;

        return px;
    }

}
