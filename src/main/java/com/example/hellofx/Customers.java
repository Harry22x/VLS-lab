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
 * Admin GUI for registering and removing customers.
 * Communicates with the VLS server via Java RMI.
 */
public class Customers extends Application {

    /** Remote service stub obtained from the RMI registry. */
    private VLSService service;

    /** Cached client list from the server: each entry is [id, fullname]. */
    private List<String[]> clientList;

    @Override
    public void start(Stage stage) {
        try {
            service = RMIClient.getService();
        } catch (Exception e) {
            showAlert("Connection Error", "Could not connect to server:\n" + e.getMessage());
            return;
        }

        Text textName       = new Text("Name:");
        Text textPhone      = new Text("Phone:");
        Text textEmail      = new Text("Email:");
        Text textRegistered = new Text("Registered:");

        TextField nameField  = new TextField();
        TextField phoneField = new TextField();
        TextField emailField = new TextField();

        ComboBox<String> comboBox = new ComboBox<>();

        Button saveButton   = new Button("Save Customer");
        Button removeButton = new Button("Remove Customer");

        // Populate combo with existing customers on load
        loadClients(comboBox);

        // ── Save Customer ─────────────────────────────────────────────────────
        saveButton.setOnAction(e -> {
            String name  = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();

            if (name.isEmpty()) { showAlert("Input Error", "Name is required."); return; }

            try {
                if (service.registerClient(name, phone, email)) {
                    showAlert("Success", "Customer '" + name + "' registered!");
                    nameField.clear(); phoneField.clear(); emailField.clear();
                    loadClients(comboBox); // refresh the remove list
                } else { showAlert("Error", "Registration failed."); }
            } catch (Exception ex) { showAlert("Error", ex.getMessage()); }
        });

        // ── Remove Customer ───────────────────────────────────────────────────
        removeButton.setOnAction(e -> {
            String selected = comboBox.getValue();
            if (selected == null) { showAlert("Input Error", "Select a customer to remove."); return; }
            int id = -1;
            for (String[] c : clientList)
                if (c[1].equals(selected)) { id = Integer.parseInt(c[0]); break; }
            try {
                if (service.removeClient(id)) {
                    showAlert("Success", "Customer removed.");
                    loadClients(comboBox);
                } else { showAlert("Error", "Failed to remove customer."); }
            } catch (Exception ex) { showAlert("Error", ex.getMessage()); }
        });

        // Layout (identical to your original)
        GridPane gridPane = new GridPane();
        gridPane.setMinSize(600, 400);
        gridPane.setPadding(new Insets(20, 20, 20, 20));
        gridPane.setVgap(12);
        gridPane.setHgap(10);
        gridPane.setAlignment(Pos.CENTER);

        gridPane.add(textName,       0, 0); gridPane.add(nameField,  1, 0);
        gridPane.add(textPhone,      0, 1); gridPane.add(phoneField, 1, 1);
        gridPane.add(textEmail,      0, 2); gridPane.add(emailField, 1, 2);
        gridPane.add(saveButton,     1, 3);
        gridPane.add(textRegistered, 0, 4); gridPane.add(comboBox,   1, 4);
        gridPane.add(removeButton,   1, 5);

        saveButton.setStyle("-fx-background-color: darkslateblue; -fx-text-fill: white; -fx-font-size:13pt;");
        removeButton.setStyle("-fx-background-color: darkslateblue; -fx-text-fill: white; -fx-font-size:13pt;");
        textName.setStyle("-fx-font: normal bold 16px 'serif'");
        textPhone.setStyle("-fx-font: normal bold 16px 'serif'");
        textEmail.setStyle("-fx-font: normal bold 16px 'serif'");
        textRegistered.setStyle("-fx-font: normal bold 16px 'serif'");
        gridPane.setStyle("-fx-background-color: BEIGE;");
        saveButton.setMaxWidth(Double.MAX_VALUE);
        removeButton.setMaxWidth(Double.MAX_VALUE);
        comboBox.setMaxWidth(Double.MAX_VALUE);
        nameField.setMaxWidth(Double.MAX_VALUE);
        phoneField.setMaxWidth(Double.MAX_VALUE);
        emailField.setMaxWidth(Double.MAX_VALUE);

        stage.setScene(new Scene(gridPane));
        stage.setTitle("Customers - Movie Library System");
        stage.show();
    }

    /**
     * Fetches active clients from the server and populates the ComboBox.
     * @param combo the target ComboBox to populate
     */
    private void loadClients(ComboBox<String> combo) {
        try {
            clientList = service.getActiveClients();
            ObservableList<String> names = FXCollections.observableArrayList();
            for (String[] c : clientList) names.add(c[1]);
            combo.setItems(names);
        } catch (Exception e) { showAlert("Error", "Could not load customers."); }
    }

    /** Displays an informational alert to the user. */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title); alert.setHeaderText(null);
        alert.setContentText(message); alert.showAndWait();
    }

    public static void main(String[] args) { launch(args); }
}