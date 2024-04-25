package com.restaurantmanagementsystem.pos.controller;

import com.restaurantmanagementsystem.pos.model.AlertUtils;
import com.restaurantmanagementsystem.pos.model.User;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminController {
    @FXML
    private Text welcomeText;
    @FXML
    private BorderPane mainBorderPane;
    private User loggedInUser;

    public void initialize() {
        welcomeText.setText("Welcome, Admin111");
        handleReportAction();
    }

    public void setUser(User user) {
        this.loggedInUser = user;
        welcomeText.setText(String.format("Welcome, %s", user.getUsername()));
    }

    private void switchToView(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent view = loader.load();
            mainBorderPane.setCenter(view);
        } catch (IOException e) {
            AlertUtils.showErrorAlert("Failed to load the view: " + fxmlFile, e.getMessage());
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
    private void handleViewOrdersAction() {
        switchToView("/com/restaurantmanagementsystem/pos/view/orders.fxml");
    }

    // Sign Out Button
    @FXML
    private void handleSignOutAction() {
        if (AlertUtils.showConfirmationAlert("Sign Out", "Are you sure you want to sign out?")) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/restaurantmanagementsystem/pos/view/login.fxml"));
                Parent loginView = loader.load();
                Stage stage = (Stage) mainBorderPane.getScene().getWindow();
                stage.setScene(new Scene(loginView));
                stage.show();
            } catch (IOException e) {
                AlertUtils.showErrorAlert("Failed to Sign Out", e.getMessage());
            }
        }
    }
}