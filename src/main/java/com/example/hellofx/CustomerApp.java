package com.example.hellofx;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Main launcher for the Customer client of the Video Library System.
 */
public class CustomerApp extends Application {

    @Override
    public void start(Stage stage) {
        new Rentals().start(stage);
    }

    public static void main(String[] args) { launch(args); }
}