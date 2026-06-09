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
 * Admin GUI for registering and removing movies.
 * Communicates with the server via Java RMI.
 */
public class Movies extends Application {

    /** Remote service stub. */
    private VLSService service;

    /** Cached genre list from server: [id, name]. */
    private List<String[]> genreList;

    /** Cached movie list from server: [id, title, genre]. */
    private List<String[]> movieList;

    @Override
    public void start(Stage stage) {
        try {
            service = RMIClient.getService();
        } catch (Exception e) {
            showAlert("Connection Error", "Could not connect to server:\n" + e.getMessage());
            return;
        }

        // Add movie section
        Text text1 = new Text("Genre:");
        ComboBox<String> genreCombo = new ComboBox<>();

        Text text2 = new Text("Name:");
        TextField titleField = new TextField();

        Button saveButton = new Button("Save Movie");

        // Remove movie section
        Text text3 = new Text("Registered:");
        ComboBox<String> movieCombo = new ComboBox<>();

        Button removeButton = new Button("Remove Movie");

        // Load data from server
        loadGenres(genreCombo);
        loadMovies(movieCombo);

        // --- Save Movie ---
        saveButton.setOnAction(e -> {
            String selectedGenre = genreCombo.getValue();
            String title = titleField.getText().trim();

            if (selectedGenre == null || title.isEmpty()) {
                showAlert("Input Error", "Please select a genre and enter a movie title.");
                return;
            }

            int genreId = -1;
            for (String[] g : genreList)
                if (g[1].equals(selectedGenre)) { genreId = Integer.parseInt(g[0]); break; }

            try {
                if (service.addMovie(title, genreId)) {
                    showAlert("Success", "Movie '" + title + "' added!");
                    titleField.clear();
                    loadMovies(movieCombo); // refresh the remove list
                } else {
                    showAlert("Error", "Failed to add movie.");
                }
            } catch (Exception ex) { showAlert("Error", ex.getMessage()); }
        });

        // --- Remove Movie ---
        removeButton.setOnAction(e -> {
            String selected = movieCombo.getValue();
            if (selected == null) { showAlert("Input Error", "Select a movie to remove."); return; }

            int movieId = -1;
            for (String[] m : movieList)
                if (m[1].equals(selected)) { movieId = Integer.parseInt(m[0]); break; }

            try {
                if (service.removeMovie(movieId)) {
                    showAlert("Success", "Movie removed.");
                    loadMovies(movieCombo);
                } else {
                    showAlert("Error", "Failed to remove movie.");
                }
            } catch (Exception ex) { showAlert("Error", ex.getMessage()); }
        });

        // Layout (same as your original)
        GridPane gridPane = new GridPane();
        gridPane.setMinSize(600, 400);
        gridPane.setPadding(new Insets(8, 8, 8, 8));
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.setAlignment(Pos.CENTER);

        gridPane.add(text1, 0, 0);   gridPane.add(genreCombo, 1, 0);
        gridPane.add(text2, 0, 1);   gridPane.add(titleField, 1, 1);
        gridPane.add(saveButton, 1, 2);
        gridPane.add(text3, 0, 3);   gridPane.add(movieCombo, 1, 3);
        gridPane.add(removeButton, 1, 4);

        // Styling (same as your original)
        saveButton.setStyle("-fx-background-color: darkslateblue; -fx-text-fill: white; -fx-font-size:13pt;");
        removeButton.setStyle("-fx-background-color: darkslateblue; -fx-text-fill: white; -fx-font-size:13pt;");
        text1.setStyle("-fx-font: normal bold 20px 'serif'");
        text2.setStyle("-fx-font: normal bold 20px 'serif'");
        text3.setStyle("-fx-font: normal bold 20px 'serif'");
        gridPane.setStyle("-fx-background-color: WHITE;");
        saveButton.setMaxWidth(Double.MAX_VALUE);
        removeButton.setMaxWidth(Double.MAX_VALUE);
        genreCombo.setMaxWidth(Double.MAX_VALUE);
        movieCombo.setMaxWidth(Double.MAX_VALUE);
        titleField.setMaxWidth(Double.MAX_VALUE);

        stage.setScene(new Scene(gridPane));
        stage.setTitle("Movies - Admin");
        stage.show();
    }

    /**
     * Fetches genres from the server and populates the combo box.
     * @param combo the target ComboBox
     */
    private void loadGenres(ComboBox<String> combo) {
        try {
            genreList = service.getAllGenres();
            ObservableList<String> names = FXCollections.observableArrayList();
            for (String[] g : genreList) names.add(g[1]);
            combo.setItems(names);
        } catch (Exception e) { showAlert("Error", "Could not load genres."); }
    }

    /**
     * Fetches active movies from the server and populates the combo box.
     * @param combo the target ComboBox
     */
    private void loadMovies(ComboBox<String> combo) {
        try {
            movieList = service.getActiveMovies();
            ObservableList<String> titles = FXCollections.observableArrayList();
            for (String[] m : movieList) titles.add(m[1]);
            combo.setItems(titles);
        } catch (Exception e) { showAlert("Error", "Could not load movies."); }
    }

    /** Displays an informational alert dialog. */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) { launch(args); }
}