package com.restaurantmanagementsystem.pos.controller;

import com.restaurantmanagementsystem.pos.db.DatabaseConnector;
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
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private TextField contactField;

    @FXML
    private TextField addressField;

    @FXML
    private Text registrationMessageLabel;

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
        String insertCustomerQuery = "INSERT INTO users (username, password, contact, address, role) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnector.getConnection()) {
            // Check if the username already exists
            if (usernameExists(conn, username)) {
                registrationMessageLabel.setText("Username already exists.");
                return false;
            }

            // Start transaction
            conn.setAutoCommit(false);

            try (PreparedStatement pstmt = conn.prepareStatement(insertCustomerQuery)) {
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                pstmt.setString(3, contact);
                pstmt.setString(4, address);
                pstmt.setString(5, "customer");

                int affectedRows = pstmt.executeUpdate();
                if (affectedRows == 0) {
                    conn.rollback();
                    return false;
                }

                conn.commit();
                return true;
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }
        } catch (SQLException ex) {
            handleSqlException(ex);
            return false;
        }
    }

    private boolean usernameExists(Connection conn, String username) throws SQLException {
        String query = "SELECT 1 FROM users WHERE username = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private void handleSqlException(SQLException ex) {
        System.err.println("An error occurred during the database operation.");
        System.err.println("SQLState: " + ex.getSQLState());
        System.err.println("Error Code: " + ex.getErrorCode());
        System.err.println("Message: " + ex.getMessage());
        ex.printStackTrace();
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

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
