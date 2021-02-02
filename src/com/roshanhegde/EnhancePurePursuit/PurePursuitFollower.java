package com.roshanhegde.EnhancePurePursuit;

import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.List;

public class PurePursuitFollower {

    public Path<Point> path = new Path<>(Point::new);
    public double forwardVel = 0;
    public double strafeVel = 0;
    public double lookahead = 100;
    public double prediction = 40;

    public double x = 0;
    public double y = 0;

    public void followPath(Path<Point> path){
        this.path = path;
    }

//    This will be substituted for mecanum driving with pid in the robotics implementation
    public void setVelocity(double forward, double strafe){
        forwardVel = forward;
        strafeVel = strafe;
    }

    public void update(){
//        change position based on forward and strafe velocities
//        also measure change in time here
    }

    public void draw(GraphicsContext gc){
//        call gc functions here
//        square should point in direction of rotation of bot

//        gc.getCanvas().getHeight()
//        gc.getCanvas().getWidth()
//        for dimensions
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

        public Action addAction(Runnable r){
            return new Action(r, this);
        }

    }

    public static class Action{
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

        public Action (Runnable r, Point parent){
            action = r;
            point = parent;
        }
    }

}
