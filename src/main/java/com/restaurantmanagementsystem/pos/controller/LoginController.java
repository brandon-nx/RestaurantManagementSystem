package com.restaurantmanagementsystem.pos.controller;

import com.restaurantmanagementsystem.pos.db.DatabaseConnector;
import java.sql.*;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Text loginMessageLabel;

    @FXML
    private Hyperlink createAccountLink;

    @FXML
    private void handleLoginButtonAction() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (authenticate(username, password)) {
            loginMessageLabel.setText("Login successful.");
        } else {
            loginMessageLabel.setText("Login failed. Please try again.");
        }
    }

    @FXML
    private void handleSwitchToRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/restaurantmanagementsystem/pos/view/register.fxml"));
            Parent registerView = loader.load();

            Stage stage = (Stage) createAccountLink.getScene().getWindow();

            stage.setScene(new Scene(registerView));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            loginMessageLabel.setText("Failed to load the registration view.");
        }
    }

    private boolean authenticate(String username, String password) {
        String loginQuery = "SELECT user_id, role FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(loginQuery)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String role = rs.getString("role");
                    int userId = rs.getInt("user_id");
                    loadDashboard(role, userId);
                    return true;
                }
            }
        } catch (SQLException e) {
            handleSqlException(e);
        }
        return false; // Login failed
    }

    private void loadDashboard(String role, int userId) {
        try {
            FXMLLoader loader = new FXMLLoader();
            String fxmlPath = "";
            String username = usernameField.getText().trim();

            if ("admin".equals(role)) {
                fxmlPath = "/com/restaurantmanagementsystem/pos/view/admin.fxml";
            } else if ("customer".equals(role)) {
                fxmlPath = "/com/restaurantmanagementsystem/pos/view/customer.fxml";
            }

            loader.setLocation(getClass().getResource(fxmlPath));
            Parent dashboardView = loader.load();

            Object controller = loader.getController();
            if (controller instanceof AdminController) {
                ((AdminController) controller).setUsername(username);
            } else if (controller instanceof CustomerController) {
                ((CustomerController) controller).setUsername(username);
            }

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(dashboardView));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            loginMessageLabel.setText("Failed to load the dashboard.");
        }
    }


    private void handleSqlException(SQLException ex) {
        System.err.println("SQLState: " + ex.getSQLState());
        System.err.println("Error Code: " + ex.getErrorCode());
        System.err.println("Message: " + ex.getMessage());
        ex.printStackTrace();
        // In a production environment, handle this more gracefully and log appropriately
    }
}
