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
    private double prevTime = System.nanoTime() / 1e9d;

    public double tolerance = 10;
//    Degrees
    public double rotationTolerance = 10;

    public double x = 0;
    public double y = 0;
    public double rot = 0;

    public void followPath(Path<Point> path) {
        this.path = path;
        currentSegment = 0;
    }

    public RMath.Point getFollowPoint() {
        Segment current = path.getSegment(currentSegment);
        RMath.Point[] lookaheadIntersections = Util.getIntersection(new Circle(x, y, lookahead), current);

//        Set lookahead point to position because we want to compare the intersections to the position
        RMath.Point position = new RMath.Point(x, y);
        RMath.Point lookaheadPoint;

        if (Util.dist(position, current.getP2()) > lookahead) {
            if (lookaheadIntersections.length > 0) {

                RMath.Point closest = lookaheadIntersections[0];
                for (RMath.Point p : lookaheadIntersections) {
//            Pick the point that has the least distance to the end of the segment
                    if (Util.dist(p, current.getP2()) < Util.dist(closest, current.getP2())) {
                        closest = p;
                    }
                }

                lookaheadPoint = closest;
            }else{
                lookaheadPoint = position.closestPoint(current);
            }
        } else {
            lookaheadPoint = current.getP2();
        }

        RMath.Point lastPoint = lookaheadPoint;
        RMath.Point total = new Point(0, 0);
        double totalDistance = 0;

        int num = currentSegment + 1;
        boolean reachedStopPoint = false;

        for(; num < path.size(); num++){
            Point point = path.getPoints().get(num);
            if(Util.dist(position, point) > lookahead + prediction) break;

            final double dist = Util.dist(lastPoint, point);
            totalDistance += dist;
            total.x += (lastPoint.x + point.x) * dist / 2;
            total.y += (lastPoint.y + point.y) * dist / 2;
            lastPoint = point;

            if(isStoppingPoint(point)){
                reachedStopPoint = true;
                break;
            }
        }


        if(!reachedStopPoint){
            RMath.Point predictionPoint;
            Segment predictionSegment = path.getSegment(num - 1);
            RMath.Point[] predictionIntersections = Util.getIntersection(new Circle(x, y, prediction + lookahead), predictionSegment);

            if (predictionIntersections.length > 0) {

                RMath.Point closest = predictionIntersections[0];
                for (RMath.Point p : predictionIntersections) {
//            Pick the point that has the least distance to the end of the segment
                    if (Util.dist(p, predictionSegment.getP2()) < Util.dist(closest, predictionSegment.getP2())) {
                        closest = p;
                    }
                }

                predictionPoint = closest;
            }else{
                predictionPoint = position.closestPoint(predictionSegment);
            }

            final double dist = Util.dist(lastPoint, predictionPoint);
            totalDistance += dist;

            total.x += (lastPoint.x + predictionPoint.x) * dist / 2;
            total.y += (lastPoint.y + predictionPoint.y) * dist / 2;
            lastPoint = predictionPoint;
        }

        Vector average = new Vector(total).scale(1 / totalDistance);
        if(totalDistance == 0){
            return lookaheadPoint;
        }

        return average.toPoint();
    }

    //    This will be substituted for mecanum driving with pid in the robotics implementation
    public void setVelocity(double forward, double strafe) {
        forwardVel = forward;
        strafeVel = strafe;
    }

    public void update() {
        RMath.Point position = new RMath.Point(x, y);

        //        also measure change in time here

        final double currentTime = System.nanoTime() / 1e9d;
        final double deltaTime = currentTime - prevTime;
        prevTime = currentTime;

        if(Util.dist(position, path.getPoints().get(currentSegment + 1)) < lookahead){
            currentSegment++;
        }

//        increment currentSegment if the endpoint is completed
//        endpoint is completed if:
//        robot is within rotation tolerance
//        robot is within lookahead
//        robot is completed with any interrupting actions

//        get the follow point
//        get the speed
//        get target rotation
//        move towards follow point at calculated speed

        RMath.Point followPoint = getFollowPoint();
        followPoint(followPoint, 100);

//        change position based on forward and strafe velocities
        Vector local = Vector.rect(strafeVel, forwardVel);
        Vector global = local.clone().setTheta(local.getTheta() - Math.toRadians(rot));
//        System.out.println(Math.toDegrees(local.getTheta() - Math.toRadians(rot)));
        Vector deltaPos = global.clone().scale(deltaTime);
        x += deltaPos.getX();
        y += deltaPos.getY();
    }

    public void manageActions(){
        Point nextPoint = path.getPoints().get(currentSegment + 1);
        Action action = nextPoint.actions.get(0);
        RMath.Point position = new RMath.Point(x, y);

        boolean withinTolerance = action.tolerance == null || Util.dist(position, nextPoint) < action.tolerance;
        boolean withinRotTolerance = action.rotationTolerance == null || nextPoint.rot == null || Math.abs(getAngleDiff(rot, nextPoint.rot)) < action.rotationTolerance;

        if(withinTolerance && withinRotTolerance){
            if(!action.interrupting){
                new Thread(action::run).start();
                nextPoint.actions.remove(0);
            }
            else{
                if(!action.started){
                    new Thread(action::run).start();
                }else if(action.started && !action.running){ // if the thread has finished
                    nextPoint.actions.remove(0);
                }
            }
        }
    }


    public double getRotation(){
//        get dist till rotation point
//        decide whether or not to go forward or go to target position based on distance
//
        return Double.NaN;
    }

    public void followPoint(RMath.Point point, double speed){
        Vector direction = new Vector(point).subtract(Vector.rect(x, y)).normalize();
        Vector localDirection = direction.clone().setTheta(direction.getTheta() + Math.toRadians(rot));

//        System.out.println(Math.toDegrees(direction.getTheta()));
//        System.out.println(direction.getMagnitude());
//        System.out.println(localDirection.getMagnitude());

        forwardVel = direction.getY() * speed;
        strafeVel  = direction.getX() * speed;
    }

    public double getSpeed(){
        return Double.NaN;
    }

    public boolean isStoppingPoint(Point p){

        if (p.rot != null && Math.abs(getAngleDiff(p.rot, rot)) >= rotationTolerance) return true;
        if (path.getPoints().indexOf(p) == path.size() - 1) return true;

        for(Action a : p.actions) {
            if (a.interrupting){
                return true;
            }
        }

        return false;
    }

    public void draw(GraphicsContext gc) {
        double robowidth = 50;
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

        gc.strokeLine(0, 0, robowidth / 2, 0);

        gc.setStroke(Color.RED);
        gc.strokeOval(0 - lookahead, 0 - lookahead, lookahead * 2, lookahead * 2);
        gc.setStroke(Color.CYAN);
        gc.strokeOval(0 - (prediction + lookahead), 0 - (prediction + lookahead), (prediction + lookahead) * 2, (prediction + lookahead) * 2);
        gc.restore();

        gc.setFill(Color.LIMEGREEN);
        RMath.Point followPoint = getFollowPoint();
        gc.fillOval(followPoint.x - 5, followPoint.y - 5, 10, 10);
    }

    /**
     * @param a1 the first angle
     * @param a2 the second angle
     * @return the shortest interval between the two angles
     */
    private double getAngleDiff(double a1, double a2) {
        a1 = Util.loop(a1, 0, 360);
        a2 = Util.loop(a2, 0, 360);

        double dist = a1 - a2;
        double shortest;
        if (Math.abs(dist) < 180)
            shortest = dist;
        else {
            if (dist > 0) shortest = dist - 360;
            else shortest = dist + 360;
        }

        return shortest;
    }

    public static class Point extends RMath.Point {
        public Double speed;
        public Double turnSpeed;
        public Double lookahead;
        public Double prediction;
        public Double rot;
        public Double rotationTolerance;

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

        public Point setRotationTolerance(Double rotationTolerance) {
            this.rotationTolerance = rotationTolerance;
            return this;
        }

        public Action addAction(Runnable r) {
            return new Action(r, this);
        }

    }

    public static class Action {
        public boolean interrupting = false;
        private boolean started = false;
        private boolean running = false;
        public Double tolerance;
        public Double rotationTolerance;
        private Runnable action;
        public final Point point;

        public boolean isStarted() {
            return started;
        }

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

        public void run(){
            running = true;
            started = true;
            action.run();
            running = false;
        }

        public Action(Runnable r, Point parent) {
            action = r;
            point = parent;
        }
    }

}
