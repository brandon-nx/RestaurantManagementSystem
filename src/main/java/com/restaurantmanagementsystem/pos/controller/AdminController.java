package com.restaurantmanagementsystem.pos.controller;

import com.restaurantmanagementsystem.pos.db.OrderDao;
import com.restaurantmanagementsystem.pos.db.OrderDaoImpl;
import com.restaurantmanagementsystem.pos.model.Order;
import com.restaurantmanagementsystem.pos.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class AdminController {
    @FXML
    private Button reportButton, inventoryButton, viewOrdersButton, signOutButton;
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
    private void handleViewOrdersAction() {
        switchToView("/com/restaurantmanagementsystem/pos/view/orders.fxml");
    }

    // Sign Out Button
    @FXML
    private void handleSignOutAction() {
        if (showConfirmationDialog("Sign Out", "Are you sure you want to sign out?")) {
            try {
                // Load the login screen.
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/restaurantmanagementsystem/pos/view/login.fxml"));
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

    private boolean showConfirmationDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}