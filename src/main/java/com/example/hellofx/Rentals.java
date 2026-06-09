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
 * Customer GUI for renting and returning movies.
 * Communicates with the VLS server via Java RMI.
 */
public class Rentals extends Application {

    /** Remote service stub obtained from the RMI registry. */
    private VLSService service;

    /** Cached client list: each entry is [id, fullname]. */
    private List<String[]> clientList;

    /** Cached genre list: each entry is [id, genre_name]. */
    private List<String[]> genreList;

    /** Cached movie list for the selected genre: each entry is [id, title]. */
    private List<String[]> movieList;

    /**
     * Cached active (unreturned) rentals for the selected customer.
     * Each entry is [rental_id, movie_title]. Used to resolve the rental ID on return.
     */
    private List<String[]> borrowedList;

    @Override
    public void start(Stage stage) {
        try {
            service = RMIClient.getService();
        } catch (Exception e) {
            showAlert("Connection Error", "Could not connect to server:\n" + e.getMessage());
            return;
        }

        Text textCustomer = new Text("Customer:");
        ComboBox<String> comboCustomer = new ComboBox<>();

        Text textGenre = new Text("Genre:");
        ComboBox<String> comboGenre = new ComboBox<>();

        Text textMovies = new Text("Movies:");
        ComboBox<String> comboMovies = new ComboBox<>();

        Text textBorrowed = new Text("Borrowed:");
        ComboBox<String> comboBorrowed = new ComboBox<>();

        Text textReturned = new Text("Returned:");
        ComboBox<String> comboReturned = new ComboBox<>();

        Button btnSave   = new Button("Save Rental");
        Button btnReturn = new Button("Return Movie");

        // Load customers, genres, and all movies on startup
        loadClients(comboCustomer);
        loadGenres(comboGenre);
        loadMovies(comboMovies, -1); // -1 means load all genres

        // ── Customer selected → refresh Borrowed and Returned combos ──────────
        comboCustomer.setOnAction(e -> {
            String selected = comboCustomer.getValue();
            if (selected != null) {
                int clientId = getClientId(selected);
                loadBorrowed(comboBorrowed, clientId);
                loadReturned(comboReturned, clientId);
            }
        });

        // ── Genre selected → filter Movies combo ──────────────────────────────
        comboGenre.setOnAction(e -> {
            String selected = comboGenre.getValue();
            if (selected == null) {
                loadMovies(comboMovies, -1); // no filter, show all
            } else {
                loadMovies(comboMovies, getGenreId(selected));
            }
        });

        // ── Save Rental ───────────────────────────────────────────────────────
        btnSave.setOnAction(e -> {
            String selCustomer = comboCustomer.getValue();
            String selMovie    = comboMovies.getValue();
            if (selCustomer == null || selMovie == null) {
                showAlert("Input Error", "Please select a customer and a movie."); return;
            }
            int clientId = getClientId(selCustomer);
            int movieId  = getMovieId(selMovie);
            try {
                if (service.rentMovie(clientId, movieId)) {
                    showAlert("Success", "Rental saved!");
                    loadBorrowed(comboBorrowed, clientId); // update Borrowed list
                } else { showAlert("Error", "Rental failed. Movie may already be rented."); }
            } catch (Exception ex) { showAlert("Error", ex.getMessage()); }
        });

        // ── Return Movie ──────────────────────────────────────────────────────
        btnReturn.setOnAction(e -> {
            String selBorrowed = comboBorrowed.getValue();
            if (selBorrowed == null) {
                showAlert("Input Error", "Select a borrowed movie to return."); return;
            }
            int rentalId = getRentalId(selBorrowed);
            try {
                if (service.returnMovie(rentalId)) {
                    showAlert("Success", "Movie returned successfully!");
                    String selCustomer = comboCustomer.getValue();
                    if (selCustomer != null) {
                        int clientId = getClientId(selCustomer);
                        loadBorrowed(comboBorrowed, clientId); // remove from Borrowed
                        loadReturned(comboReturned, clientId); // add to Returned
                    }
                } else { showAlert("Error", "Return failed."); }
            } catch (Exception ex) { showAlert("Error", ex.getMessage()); }
        });

        // Layout (identical to your original)
        GridPane gridPane = new GridPane();
        gridPane.setMinSize(600, 600);
        gridPane.setPadding(new Insets(20));
        gridPane.setVgap(15);
        gridPane.setHgap(15);
        gridPane.setAlignment(Pos.CENTER);

        gridPane.add(textCustomer, 0, 0); gridPane.add(comboCustomer, 1, 0);
        gridPane.add(textGenre,    0, 1); gridPane.add(comboGenre,    1, 1);
        gridPane.add(textMovies,   0, 2); gridPane.add(comboMovies,   1, 2);
        gridPane.add(btnSave,      1, 3);
        gridPane.add(textBorrowed, 0, 4); gridPane.add(comboBorrowed, 1, 4);
        gridPane.add(btnReturn,    1, 5);
        gridPane.add(textReturned, 0, 6); gridPane.add(comboReturned, 1, 6);

        String btnStyle   = "-fx-background-color: darkslateblue; -fx-text-fill: white; -fx-font-size:13pt;";
        String labelStyle = "-fx-font: normal bold 20px 'serif'";
        btnSave.setStyle(btnStyle);   btnReturn.setStyle(btnStyle);
        textCustomer.setStyle(labelStyle); textGenre.setStyle(labelStyle);
        textMovies.setStyle(labelStyle);   textBorrowed.setStyle(labelStyle);
        textReturned.setStyle(labelStyle);
        gridPane.setStyle("-fx-background-color: WHITE;");

        double fieldWidth = 250;
        comboCustomer.setPrefWidth(fieldWidth); comboGenre.setPrefWidth(fieldWidth);
        comboMovies.setPrefWidth(fieldWidth);   comboBorrowed.setPrefWidth(fieldWidth);
        comboReturned.setPrefWidth(fieldWidth);
        btnSave.setMaxWidth(Double.MAX_VALUE);  btnReturn.setMaxWidth(Double.MAX_VALUE);

        stage.setScene(new Scene(gridPane));
        stage.setTitle("Rentals");
        stage.show();
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    /**
     * Fetches active clients from server and populates the Customer ComboBox.
     * @param combo the Customer ComboBox
     */
    private void loadClients(ComboBox<String> combo) {
        try {
            clientList = service.getActiveClients();
            ObservableList<String> names = FXCollections.observableArrayList();
            for (String[] c : clientList) names.add(c[1]);
            combo.setItems(names);
        } catch (Exception e) { showAlert("Error", "Could not load customers."); }
    }

    /**
     * Fetches active genres from server and populates the Genre ComboBox.
     * @param combo the Genre ComboBox
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
     * Fetches movies from server — all movies if genreId is -1, or filtered by genre.
     * @param combo   the Movies ComboBox
     * @param genreId the genre to filter by, or -1 for no filter
     */
    private void loadMovies(ComboBox<String> combo, int genreId) {
        try {
            movieList = (genreId == -1)
                    ? service.getActiveMovies()      // returns [id, title, genre]
                    : service.getMoviesByGenre(genreId); // returns [id, title]
            ObservableList<String> titles = FXCollections.observableArrayList();
            for (String[] m : movieList) titles.add(m[1]); // title is always index 1
            combo.setItems(titles);
        } catch (Exception e) { showAlert("Error", "Could not load movies."); }
    }

    /**
     * Fetches unreturned rentals for a client and populates the Borrowed ComboBox.
     * @param combo    the Borrowed ComboBox
     * @param clientId the selected client's ID
     */
    private void loadBorrowed(ComboBox<String> combo, int clientId) {
        try {
            borrowedList = service.getActiveRentals(clientId);
            ObservableList<String> titles = FXCollections.observableArrayList();
            for (String[] r : borrowedList) titles.add(r[1]);
            combo.setItems(titles);
        } catch (Exception e) { showAlert("Error", "Could not load borrowed movies."); }
    }

    /**
     * Fetches returned rentals for a client and populates the Returned ComboBox.
     * @param combo    the Returned ComboBox (read-only display)
     * @param clientId the selected client's ID
     */
    private void loadReturned(ComboBox<String> combo, int clientId) {
        try {
            List<String[]> returnedList = service.getReturnedRentals(clientId);
            ObservableList<String> titles = FXCollections.observableArrayList();
            for (String[] r : returnedList) titles.add(r[1]);
            combo.setItems(titles);
        } catch (Exception e) { showAlert("Error", "Could not load returned movies."); }
    }

    /** Looks up a client's ID from the cached list by name. */
    private int getClientId(String name) {
        for (String[] c : clientList) if (c[1].equals(name)) return Integer.parseInt(c[0]);
        return -1;
    }

    /** Looks up a genre's ID from the cached list by name. */
    private int getGenreId(String name) {
        for (String[] g : genreList) if (g[1].equals(name)) return Integer.parseInt(g[0]);
        return -1;
    }

    /** Looks up a movie's ID from the cached list by title. */
    private int getMovieId(String title) {
        for (String[] m : movieList) if (m[1].equals(title)) return Integer.parseInt(m[0]);
        return -1;
    }

    /** Looks up a rental's ID from the borrowed list by movie title. */
    private int getRentalId(String title) {
        for (String[] r : borrowedList) if (r[1].equals(title)) return Integer.parseInt(r[0]);
        return -1;
    }

    /** Displays an informational alert to the user. */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title); alert.setHeaderText(null);
        alert.setContentText(message); alert.showAndWait();
    }

    public static void main(String[] args) { launch(args); }
}