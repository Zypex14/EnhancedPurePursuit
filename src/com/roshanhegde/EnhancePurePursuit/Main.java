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
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application {

    private GraphicsContext gc;
    private final double initialHeight = 600;
    private final double initialWidth = 600;
    private Path<PurePursuitFollower.Point> path;

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Hello World");

        Canvas canvas = new Canvas(initialWidth, initialHeight);
        Group root = new Group(canvas);

        gc = canvas.getGraphicsContext2D();

        Timeline mainLoop = new Timeline();
        mainLoop.setCycleCount(Timeline.INDEFINITE);

        KeyFrame kf = new KeyFrame(Duration.seconds(1d / 60),
                (ActionEvent event) -> {
                    onUpdate();
                }
        );

        mainLoop.getKeyFrames().add(kf);
        mainLoop.play();

        primaryStage.setScene(new Scene(root, initialWidth, initialHeight));
        primaryStage.show();

        onInit();
    }

    private void onInit() {
        path = new Path<>();
        path.addPoint(0, 0).setLookahead(100d).setPrediction(20d);
        path.addPoint(100, 0).setSpeed(4d);
        path.addPoint(0, 100)
                .addAction(() -> System.out.println("testing"))
                .setInterrupting(true)
                .setRotationTolerance(10d);
        path.addPoint(-100, 100);
    }

    private void onUpdate() {
        final double w = gc.getCanvas().getHeight();
        final double h = gc.getCanvas().getWidth();

        System.out.println("height: " + h);

        path.draw(gc);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
