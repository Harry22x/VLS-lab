package com.example.hellofx;

import com.example.hellofx.rmi.RMIClient;
import com.example.hellofx.rmi.VLSService;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.util.List;

/**
 * Admin GUI for adding and removing movie genres.
 * Communicates with the VLS server via Java RMI.
 */
public class Genres extends Application {

    /** Remote service stub obtained from the RMI registry. */
    private VLSService service;

    /** Cached genre list from the server: each entry is [id, genre_name]. */
    private List<String[]> genreList;

    @Override
    public void start(Stage stage) {
        try {
            service = RMIClient.getService();
        } catch (Exception e) {
            showAlert("Connection Error", "Could not connect to server:\n" + e.getMessage());
            return;
        }

        Text text1 = new Text("Name:");
        Text text2 = new Text("Registered:");
        TextField textField1 = new TextField();
        ComboBox<String> comboBox = new ComboBox<>();
        Button button1 = new Button("Save");
        Button button2 = new Button("Remove");

        // Populate combo with existing genres on load
        loadGenres(comboBox);

        // ── Save Genre ────────────────────────────────────────────────────────
        button1.setOnAction(e -> {
            String name = textField1.getText().trim();
            if (name.isEmpty()) { showAlert("Input Error", "Please enter a genre name."); return; }
            try {
                if (service.addGenre(name)) {
                    showAlert("Success", "Genre '" + name + "' saved!");
                    textField1.clear();
                    loadGenres(comboBox); // refresh the remove list
                } else { showAlert("Error", "Failed to save genre."); }
            } catch (Exception ex) { showAlert("Error", ex.getMessage()); }
        });

        // ── Remove Genre ──────────────────────────────────────────────────────
        button2.setOnAction(e -> {
            String selected = comboBox.getValue();
            if (selected == null) { showAlert("Input Error", "Select a genre to remove."); return; }
            int id = -1;
            for (String[] g : genreList)
                if (g[1].equals(selected)) { id = Integer.parseInt(g[0]); break; }
            try {
                if (service.removeGenre(id)) {
                    showAlert("Success", "Genre removed.");
                    loadGenres(comboBox);
                } else { showAlert("Error", "Failed to remove genre."); }
            } catch (Exception ex) { showAlert("Error", ex.getMessage()); }
        });

        // Layout (identical to your original)
        GridPane gridPane = new GridPane();
        gridPane.setMinSize(600, 400);
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.setAlignment(Pos.CENTER);

        gridPane.add(text1, 0, 0);     gridPane.add(textField1, 1, 0);
        gridPane.add(button1, 1, 1);
        gridPane.add(text2, 0, 2);     gridPane.add(comboBox, 1, 2);
        gridPane.add(button2, 1, 3);

        button1.setStyle("-fx-background-color: darkslateblue; -fx-text-fill: white; -fx-font-size:13pt;");
        button2.setStyle("-fx-background-color: darkslateblue; -fx-text-fill: white; -fx-font-size:13pt;");
        text1.setStyle("-fx-font: normal bold 20px 'serif'");
        text2.setStyle("-fx-font: normal bold 20px 'serif'");
        gridPane.setStyle("-fx-background-color: BEIGE;");
        button1.setMaxWidth(Double.MAX_VALUE);
        button2.setMaxWidth(Double.MAX_VALUE);
        comboBox.setMaxWidth(Double.MAX_VALUE);
        textField1.setMaxWidth(Double.MAX_VALUE);

        stage.setScene(new Scene(gridPane));
        stage.setTitle("Genres - Movie Library System");
        stage.show();
    }

    /**
     * Fetches active genres from the server and populates the ComboBox.
     * @param combo the target ComboBox to populate
     */
    private void loadGenres(ComboBox<String> combo) {
        try {
            genreList = service.getAllGenres();
            ObservableList<String> names = FXCollections.observableArrayList();
            for (String[] g : genreList) names.add(g[1]);
            combo.setItems(names);
        } catch (Exception e) { showAlert("Error", "Could not load genres."); }
    }

    /** Displays an informational alert to the user. */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title); alert.setHeaderText(null);
        alert.setContentText(message); alert.showAndWait();
    }

    public static void main(String[] args) { launch(args); }
}