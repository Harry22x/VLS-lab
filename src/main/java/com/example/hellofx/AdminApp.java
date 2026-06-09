package com.example.hellofx;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Main launcher for the Admin client of the Video Library System.
 */
public class AdminApp extends Application {

    @Override
    public void start(Stage stage) {
        Text title = new Text("Admin Dashboard");
        title.setStyle("-fx-font: normal bold 24px 'serif'");

        Button moviesBtn = new Button("Manage Movies");
        Button clientsBtn = new Button("Register Customers");

        moviesBtn.setOnAction(e -> new Movies().start(new Stage()));
        clientsBtn.setOnAction(e -> new Customers().start(new Stage()));

        moviesBtn.setStyle("-fx-background-color: darkslateblue; -fx-text-fill: white; -fx-font-size:13pt;");
        clientsBtn.setStyle("-fx-background-color: darkslateblue; -fx-text-fill: white; -fx-font-size:13pt;");
        moviesBtn.setMaxWidth(Double.MAX_VALUE);
        clientsBtn.setMaxWidth(Double.MAX_VALUE);

        VBox vbox = new VBox(20, title, moviesBtn, clientsBtn);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(30));
        vbox.setStyle("-fx-background-color: WHITE;");

        stage.setScene(new Scene(vbox, 400, 300));
        stage.setTitle("VLS - Admin");
        stage.show();
    }

    public static void main(String[] args) { launch(args); }
}