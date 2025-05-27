package com.abadou.ui;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TrafficApp extends Application {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 800;
    private static final int ROAD_WIDTH = 100;
    private static final int FEU_SIZE = 20;

    private static final Map<String, double[]> vehiclePositions = new ConcurrentHashMap<>();
    private static final Map<String, String> feuStates = new ConcurrentHashMap<>();

    public static void updateVehicle(String name, double x, double y) {
        vehiclePositions.put(name, new double[]{x, y});
    }

    public static void updateFeu(String feuName, String etat) {
        feuStates.put(feuName, etat);
    }

    public static boolean isFeuVert(String feuName) {
        return "VERT".equalsIgnoreCase(feuStates.getOrDefault(feuName, "ROUGE"));
    }

    @Override
    public void start(Stage stage) {
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        AnimationTimer timer = new AnimationTimer() {
            public void handle(long now) {
                drawScene(gc);
            }
        };
        timer.start();

        stage.setTitle("Simulation Trafic Intelligent");
        stage.setScene(new Scene(new StackPane(canvas)));
        stage.show();
    }

    private void drawScene(GraphicsContext gc) {
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        // ROUTES
        gc.setFill(Color.DARKGRAY);
        gc.fillRect(WIDTH / 2 - ROAD_WIDTH / 2, 0, ROAD_WIDTH, HEIGHT); // verticale
        gc.fillRect(0, HEIGHT / 2 - ROAD_WIDTH / 2, WIDTH, ROAD_WIDTH); // horizontale

        // LIGNES CENTRALES
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);
        gc.strokeLine(WIDTH / 2, 0, WIDTH / 2, HEIGHT);   // verticale
        gc.strokeLine(0, HEIGHT / 2, WIDTH, HEIGHT / 2);  // horizontale

        // LIGNES D'ARRÊT
        gc.setStroke(Color.RED);
        gc.setLineWidth(1);
        gc.strokeLine(390, 340, 410, 340); // Nord
        gc.strokeLine(390, 460, 410, 460); // Sud
        gc.strokeLine(340, 390, 340, 410); // Ouest
        gc.strokeLine(460, 390, 460, 410); // Est

        // FEUX
        drawFeu(gc, "FeuN", 330, 330); // en haut à droite
        drawFeu(gc, "FeuS", 450, 450); // en bas à droite
        drawFeu(gc, "FeuE", 450, 330); // à droite en haut
        drawFeu(gc, "FeuO", 330, 450); // à gauche en haut

        // VÉHICULES
        drawVehicles(gc);
    }

    private void drawFeu(GraphicsContext gc, String name, double x, double y) {
        String etat = feuStates.getOrDefault(name, "ROUGE");

        gc.setFill(
                "VERT".equalsIgnoreCase(etat) ? Color.GREEN :
                        "ORANGE".equalsIgnoreCase(etat) ? Color.ORANGE :
                                Color.RED
        );

        gc.fillOval(x, y, FEU_SIZE, FEU_SIZE);
    }

    private void drawVehicles(GraphicsContext gc) {
        gc.setFill(Color.BLUE);

        for (Map.Entry<String, double[]> entry : vehiclePositions.entrySet()) {
            String name = entry.getKey();
            double[] pos = entry.getValue();
            double x = pos[0];
            double y = pos[1];

            // Identifier le sens du véhicule par son nom
            boolean estVertical = name.toUpperCase().contains("NORD") || name.toUpperCase().contains("SUD");
            boolean estHorizontal = name.toUpperCase().contains("EST") || name.toUpperCase().contains("OUEST");

            if (estVertical) {
                // Véhicule Nord/Sud → vertical
                gc.fillRect(x - 5, y - 15, 10, 30);
            } else if (estHorizontal) {
                // Véhicule Est/Ouest → horizontal
                gc.fillRect(x - 15, y - 5, 30, 10);
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
