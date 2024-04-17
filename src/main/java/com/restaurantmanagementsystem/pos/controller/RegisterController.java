package com.restaurantmanagementsystem.pos.controller;

import com.restaurantmanagementsystem.pos.db.DatabaseConnector;
import com.restaurantmanagementsystem.pos.db.RegisterDao;
import com.restaurantmanagementsystem.pos.db.RegisterDaoImpl;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegisterController {

    @FXML
    private TextField usernameField, contactField, addressField;
    @FXML
    private PasswordField passwordField, confirmPasswordField;
    @FXML
    private Text registrationMessageLabel;
    private RegisterDao registerDao = new RegisterDaoImpl();

    @FXML
    private void handleRegisterButtonAction() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();
        String contact = contactField.getText().trim();
        String address = addressField.getText().trim();


        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || contact.isEmpty() || address.isEmpty()) {
            registrationMessageLabel.setText("Please complete all fields.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            registrationMessageLabel.setText("Passwords do not match.");
            return;
        }

        if (!contact.matches("\\d{10}")) {
            registrationMessageLabel.setText("Please enter a valid contact number.");
            return;
        }

        if (registerCustomer(username, password, contact, address)) {
            showAlert("Registration Successful", "You have been registered successfully!");
            handleSwitchToLogin();
        }
    }

    private boolean registerCustomer(String username, String password, String contact, String address) {
        // Register customer using the DAO
        if (!registerDao.checkUsernameExists(username)) {
            return registerDao.registerCustomer(username, password, contact, address);
        } else {
            registrationMessageLabel.setText("Username already exists.");
            return false;
        }
    }

    @FXML
    private void handleSwitchToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/restaurantmanagementsystem/pos/view/login.fxml"));
            Parent loginView = loader.load();

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(loginView));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            registrationMessageLabel.setText("Failed to load the login view.");
        }
    }

    private void handleSqlException(SQLException ex) {
        System.err.println("An error occurred during the database operation.");
        System.err.println("SQLState: " + ex.getSQLState());
        System.err.println("Error Code: " + ex.getErrorCode());
        System.err.println("Message: " + ex.getMessage());
        ex.printStackTrace();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
