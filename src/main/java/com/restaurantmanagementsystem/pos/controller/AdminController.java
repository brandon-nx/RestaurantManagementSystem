package com.restaurantmanagementsystem.pos.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Optional;

public class AdminController {
    @FXML
    private Button reportButton, inventoryButton, menuButton, signOutButton;
    @FXML
    private Text welcomeText;
    @FXML
    private BorderPane mainBorderPane;

    public void initialize() {
        welcomeText.setText("Welcome, Admin111");
    }

    public void setUsername(String username) {
        welcomeText.setText("Welcome, " + username);
    }

    private void switchToView(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent view = loader.load();
            mainBorderPane.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the error properly
        }
    }

    @FXML
    private void handleReportAction() {
        switchToView("/com/restaurantmanagementsystem/pos/view/report.fxml");
    }

    @FXML
    private void handleInventoryAction() {
        switchToView("/com/restaurantmanagementsystem/pos/view/inventory.fxml");
    }

    @FXML
    private void handleMenuAction() {
        switchToView("/com/restaurantmanagementsystem/pos/view/menu.fxml");
    }

    // Sign Out Button
    @FXML
    private void handleSignOutAction() {
        // Create a confirmation dialog.
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Sign Out");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Are you sure you want to sign out?");

        // Show the alert and wait for the user's response.
        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // If the user confirms, sign out and load the login view.
            try {
                // Load the login screen.
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/restaurantmanagementsystem/pos/view/login.fxml")); // Correct the path if necessary.
                Parent loginView = loader.load();

                // Get the current stage (window) from any component.
                Stage stage = (Stage) signOutButton.getScene().getWindow();
                stage.setScene(new Scene(loginView));
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
                // Here, set a message or log the error as appropriate.
            }
        }
    }
}