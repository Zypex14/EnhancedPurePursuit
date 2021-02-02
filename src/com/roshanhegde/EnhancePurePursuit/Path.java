package com.roshanhegde.EnhancePurePursuit;

import RMath.Point;
import RMath.Segment;
import RMath.Util;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.*;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.paint.Color;
// This class uses a generic type so the Point container can have more data than just x and y
public class Path<P extends Point> {

    List<P> points = new ArrayList<>();

    public double getRemainingDist(double x, double y){
        return 1/0;
    }

    public P addPoint(double x, double y){
//        this is a problem here because I want to initialize the point, but its not guaranteed that
//        a child of point will have a constructor that meets this criteria

//        A solution would be just to pass in an interface for a factory that instantiates out point, but thats clunky
        P point = new P(x, y);
        points.add(point);
        
        return point;
    }

    public Segment getSegment(int first){
        first = (int)Util.cap(first, 0,points.size() - 2);
        RMath.Point p1 = new RMath.Point(points.get(first).x, points.get(first).y);
        RMath.Point p2 = new RMath.Point(points.get(first + 1).x, points.get(first + 1).y);

        return new Segment(p1, p2);
    }

    public void draw(GraphicsContext gc){
//        draw a black polyline with small circles at each segment

        for(int i = 0; i < points.size(); i++){
            double pointAX = points.get(i).x;
            double pointAY = points.get(i).y;
            double pointBX = points.get(i+1).x;
            double pointBY = points.get(i+1).y;
            Line line = new Line(pointAX, pointAY, pointBX, pointBY);
            Circle circle = new Circle(pointAX, pointAY, 10);
            circle.setFill(Color.BLACK);
        }
    }

}
