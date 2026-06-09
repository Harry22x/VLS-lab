package com.example.hellofx;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Main entry point for the Video Library System.
 * Shows a dashboard for both Admin and Customer roles.
 * On the Admin PC (PC 1) run this after VLSServer.
 * On the Customer PC (PC 2) run this and use only the Customer section.
 */
public class Launcher extends Application {

    @Override
    public void start(Stage stage) {
        Text appTitle = new Text("Video Library System");
        appTitle.setStyle("-fx-font: normal bold 26px 'serif'");

        // ── Admin section ─────────────────────────────────────────────────────
        Text adminLabel = new Text("Admin");
        adminLabel.setStyle("-fx-font: normal bold 14px 'serif'");

        Button btnGenres    = new Button("Manage Genres");
        Button btnMovies    = new Button("Manage Movies");
        Button btnCustomers = new Button("Manage Customers");

        String adminStyle = "-fx-background-color: darkslateblue; -fx-text-fill: white; -fx-font-size:12pt;";
        btnGenres.setStyle(adminStyle);
        btnMovies.setStyle(adminStyle);
        btnCustomers.setStyle(adminStyle);

        btnGenres.setOnAction(e -> new Genres().start(new Stage()));
        btnMovies.setOnAction(e -> new Movies().start(new Stage()));
        btnCustomers.setOnAction(e -> new Customers().start(new Stage()));

        // ── Customer section ──────────────────────────────────────────────────
        Text customerLabel = new Text("Customer");
        customerLabel.setStyle("-fx-font: normal bold 14px 'serif'");

        Button btnRentals = new Button("Rent / Return Movies");
        btnRentals.setStyle("-fx-background-color: teal; -fx-text-fill: white; -fx-font-size:12pt;");
        btnRentals.setOnAction(e -> new Rentals().start(new Stage()));

        // Make all buttons the same width
        for (Button b : new Button[]{ btnGenres, btnMovies, btnCustomers, btnRentals })
            b.setMaxWidth(Double.MAX_VALUE);

        VBox vbox = new VBox(10,
                appTitle,
                new Separator(),
                adminLabel, btnGenres, btnMovies, btnCustomers,
                new Separator(),
                customerLabel, btnRentals
        );
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(30));
        vbox.setStyle("-fx-background-color: WHITE;");

        stage.setScene(new Scene(vbox, 420, 400));
        stage.setTitle("VLS Dashboard");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}