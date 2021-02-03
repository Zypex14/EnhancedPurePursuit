package com.roshanhegde.EnhancePurePursuit;

import RMath.Circle;
import RMath.Segment;
import RMath.Util;
import RMath.Vector;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class PurePursuitFollower {

    public Path<Point> path = new Path<>(Point::new);
    public double forwardVel = 0;
    public double strafeVel = 0;
    public double lookahead = 100;
    public double prediction = 40;
    private int currentSegment = 0;

    public double x = 0;
    public double y = 0;

    public void followPath(Path<Point> path) {
        this.path = path;
        currentSegment = 0;
    }

    public RMath.Point getFollowPoint() {
        Segment current = path.getSegment(currentSegment);
        RMath.Point[] lookaheadIntersections = Util.getIntersection(new Circle(x, y, lookahead), current);

//        Set lookahead point to position because we want to compare the intersections to the position
        RMath.Point position = new RMath.Point(x, y);
        RMath.Point lookaheadPoint = position;

        if (lookaheadIntersections.length > 0) {
            boolean foundIntersection = false;
            for (RMath.Point p : lookaheadIntersections) {
//            Pick the point that has the least distance to the end of the segment
                if (Util.dist(p, current.getP2()) < Util.dist(lookaheadPoint, current.getP2())) {
                    lookaheadPoint = p;
                    foundIntersection = true;
                }
            }

//        If the endpoint is already within the lookeahead circle, just return the endPoint
            if (!foundIntersection) {
                return current.getP2();
            }
        } else {
            return position.closestPoint(current);
        }

        return lookaheadPoint;

//        RMath.Point lastPoint = lookaheadPoint;
//        RMath.Point total = new Point(0, 0);
//        int num = 1;
//
//        for (int i = currentSegment + 1; i < path.getPoints().size(); i++) {
//            RMath.Point point = path.getPoints().get(i);
//            if (Util.dist(position, point) < lookahead + prediction) {
//                total.x += (point.x + lastPoint.x) / 2;
//                total.y += (point.y + lastPoint.y) / 2;
//                num++;
//                lastPoint = point;
//            } else {
//                break;
//            }
//        }
//
//        RMath.Point predictionPoint;
//
//        if (num + currentSegment - 1 >= path.getPoints().size()) {
//            predictionPoint = path.getPoints().get(path.getPoints().size() - 1);
//        } else {
//            Segment segment = path.getSegment(num + currentSegment);
//            RMath.Point[] predictionIntersections = Util.getIntersection(new Circle(x, y, lookahead + prediction), segment);
//            if (predictionIntersections.length == 0)
//                return lookaheadPoint;
//
//            predictionPoint = predictionIntersections[0];
//            for (RMath.Point p : predictionIntersections) {
////            Pick the point that has the least distance to the end of the segment
//                if (Util.dist(p, segment.getP2()) < Util.dist(predictionPoint, segment.getP2())) {
//                    predictionPoint = p;
//                }
//            }
//        }
//
//        total.x += (lastPoint.x - predictionPoint.x) / 2;
//        total.y += (lastPoint.y - predictionPoint.y) / 2;
//
//        RMath.Point followPoint = new Point(lastPoint.x / num, lastPoint.y / num);
//        return followPoint;
    }

    //    This will be substituted for mecanum driving with pid in the robotics implementation
    public void setVelocity(double forward, double strafe) {
        forwardVel = forward;
        strafeVel = strafe;
    }

    public void update() {
//        change position based on forward and strafe velocities
//        also measure change in time here
    }

    public void draw(GraphicsContext gc) {
        double robowidth = 50;
        //temp
        double rot = 0;
//        call gc functions here
//        square should point in direction of rotation of bot

//        gc.getCanvas().getHeight()
//        gc.getCanvas().getWidth()
//        for dimensions
        gc.save();
        gc.translate(x, y);
        gc.rotate(rot);
        gc.setStroke(Color.BLUE);
        gc.strokeRect(0 - robowidth / 2, 0 - robowidth / 2, robowidth, robowidth);
        gc.setStroke(Color.RED);
        gc.strokeOval(0 - lookahead, 0 - lookahead, lookahead * 2, lookahead * 2);
        gc.setStroke(Color.CYAN);
        gc.strokeOval(0 - (prediction + lookahead), 0 - (prediction + lookahead), (prediction + lookahead) * 2, (prediction + lookahead) * 2);
        gc.setFill(Color.GREEN);
        RMath.Point followPoint = getFollowPoint();
        gc.fillOval(followPoint.x - 5, followPoint.y - 5, 10, 10);
        gc.restore();
    }

    public static class Point extends RMath.Point {
        Double speed;
        Double turnSpeed;
        Double lookahead;
        Double prediction;
        Double rot;

        List<Action> actions = new ArrayList<>();

        public Point(double x, double y) {
            super(x, y);
        }

        public Point setSpeed(Double speed) {
            this.speed = speed;
            return this;
        }

        public Point setTurnSpeed(Double turnSpeed) {
            this.turnSpeed = turnSpeed;
            return this;
        }

        public Point setLookahead(Double lookahead) {
            this.lookahead = lookahead;
            return this;
        }

        public Point setPrediction(Double prediction) {
            this.prediction = prediction;
            return this;
        }

        public Point setRot(Double rot) {
            this.rot = rot;
            return this;
        }

        public Action addAction(Runnable r) {
            return new Action(r, this);
        }

    }

    public static class Action {
        public boolean interrupting = false;
        public Double tolerance;
        public Double rotationTolerance;
        public Runnable action;
        public final Point point;

        public Action setInterrupting(boolean interrupting) {
            this.interrupting = interrupting;
            return this;
        }

        public Action setTolerance(Double tolerance) {
            this.tolerance = tolerance;
            return this;
        }

        public Action setRotationTolerance(Double rotationTolerance) {
            this.rotationTolerance = rotationTolerance;
            return this;
        }

        public Action(Runnable r, Point parent) {
            action = r;
            point = parent;
        }
    }

}
