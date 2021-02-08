package com.roshanhegde.EnhancePurePursuit;

import RMath.Point;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    private GraphicsContext gc;
    private final double initialHeight = 600;
    private final double initialWidth = 600;
    private Path<PurePursuitFollower.Point> path;
    private PurePursuitFollower robot;
    private Point mousePos = new Point(0, 0);
    private List<Point> trail = new ArrayList<>();
   // private PurePursuitFollower bot = new PurePursuitFollower();

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Hello World");

        Canvas canvas = new Canvas(initialWidth, initialHeight);
        Group root = new Group(canvas);

        gc = canvas.getGraphicsContext2D();
        gc.setImageSmoothing(true);

        Timeline mainLoop = new Timeline();
        mainLoop.setCycleCount(Timeline.INDEFINITE);

        KeyFrame kf = new KeyFrame(Duration.seconds(1d / 60),
                (ActionEvent event) -> onUpdate()
        );


        mainLoop.getKeyFrames().add(kf);
        mainLoop.play();

        Scene scene = new Scene(root, initialWidth, initialHeight);

//        Listeners to update the canvas dimensions as the window is resized
        scene.widthProperty().addListener((observableValue, oldSceneWidth, newSceneWidth) -> canvas.setWidth((double) newSceneWidth));
        scene.heightProperty().addListener((observableValue, oldSceneHeight, newSceneHeight) -> canvas.setHeight((double) newSceneHeight));

        scene.setOnMouseMoved(mouseEvent -> {
            mousePos.x = mouseEvent.getX();
            mousePos.y = mouseEvent.getY();
        });

        primaryStage.setScene(scene);
        primaryStage.show();

        onInit();
    }

    private void onInit() {
        path = new Path<>(PurePursuitFollower.Point::new);
        path.addPoint(100, 100).setLookahead(100d).setPrediction(20d);
        path.addPoint(300, 100).setSpeed(4d);
        path.addPoint(500, 300)
                .addAction(() -> System.out.println("testing"))
                .setInterrupting(true)
                .setRotationTolerance(10d);
        path.addPoint(200, 500);
        path.addPoint(100, 300);
        path.addPoint(100, 200);
        path.addPoint(500, 200);

        robot = new PurePursuitFollower();
        robot.lookahead = 50;
        robot.followPath(path);

//        robot.x = 300;
//        robot.y = 300;
//        robot.forwardVel = 20;
//        robot.rot = 45;

    }

    private void onUpdate() {
        final double w = gc.getCanvas().getWidth();
        final double h = gc.getCanvas().getHeight();

        gc.clearRect(0, 0, w, h);
        robot.update();
        trail.add(new Point(robot.x, robot.y));

        path.draw(gc);

        gc.setStroke(Color.LIMEGREEN);
        gc.beginPath();
        gc.moveTo(trail.get(0).x, trail.get(0).y);
        for(Point p : trail) gc.lineTo(p.x, p.y);
        gc.stroke();
        gc.closePath();
        gc.setStroke(Color.BLACK);

        robot.draw(gc);

//        robot.x = mousePos.x;
//        robot.y = mousePos.y;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
