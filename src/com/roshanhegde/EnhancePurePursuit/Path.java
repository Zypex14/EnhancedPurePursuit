package com.roshanhegde.EnhancePurePursuit;

import RMath.Point;
import RMath.Segment;
import RMath.Util;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.paint.Color;

import javax.swing.*;

// This class uses a generic type so the Point container can have more data than just x and y
public class Path<P extends Point> {

    private List<P> points = new ArrayList<>();
    private PointFactory<P> factory;

    public Path(PointFactory<P> factory){
        this.factory = factory;
    }

    public double size(){
        return points.size();
    }

    public double getRemainingDist(Point follower, int segIndex){
        double totalDist = 0;
        Segment s = getSegment(segIndex);

        Point closest = follower.closestPoint(s);
        totalDist += RMath.Util.dist(follower, closest);
        totalDist += RMath.Util.dist(closest, s.getP2());


        for (int i = segIndex + 1; i < points.size() - 2; i++) {
            totalDist += getSegment(i).getLength();
        }
        return totalDist;
    }

    public double distToPoint(Point follower, int segIndex, PointChecker<P> checker){
        double totalDist = 0;
        Segment s = getSegment(segIndex);

        Point closest = follower.closestPoint(s);
        totalDist += RMath.Util.dist(follower, closest);
        totalDist += RMath.Util.dist(closest, getSegment(segIndex).getP2());

        if(checker.checkPoint(points.get(segIndex + 1))){
            return totalDist;
        }

        for (int i = 1; i < points.size() - 2; i++) {
            totalDist += getSegment(i).getLength();
            if(checker.checkPoint(points.get(i + 1))){
                return totalDist;
            }
        }
        return 0;


    }

    public List<P> getPoints(){
        return points;
    }

    public P addPoint(double x, double y){
//        this is a problem here because I want to initialize the point, but its not guaranteed that
//        a child of point will have a constructor that meets this criteria

//        A solution would be just to pass in an interface for a factory that instantiates out point, but thats clunky
        P point = factory.createPoint(x, y);
        points.add(point);

        return point;
    }

    public Segment getSegment(int first){
        first = (int)Util.cap(first, 0,points.size() - 2);
        RMath.Point p1 = new RMath.Point(points.get(first).x, points.get(first).y);
        RMath.Point p2 = new RMath.Point(points.get(first + 1).x, points.get(first + 1).y);

        return new Segment(p1, p2);
    }

    public List<Segment> toSegments(){
        List<Segment> out = new ArrayList<>();
        for(int i = 0; i < points.size() - 1; i++){
            out.add(new Segment(points.get(i), points.get(i + 1)));
        }
        return out;
    }


    public void draw(GraphicsContext gc){
        double pointSize = 5;
        int size = points.size();
        double[] xValues = new double[size];
        double[] yValues = new double[size];
        gc.setFill(Color.BLACK);
        //System.out.println(points.size());
        for(int i = 0; i < points.size(); i++){
            xValues[i] = points.get(i).x;
            yValues[i] = points.get(i).y;
            //System.out.println("Added Point :" + i);
            gc.fillOval(points.get(i).x - pointSize/2, points.get(i).y - pointSize/2, pointSize, pointSize);

        }
        gc.strokePolyline(xValues, yValues, size);

    }

    public interface PointFactory<T>{
        T createPoint(double x, double y);
    }

    public interface PointChecker<T>{
        boolean checkPoint(T point);
    }

}
