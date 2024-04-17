package com.restaurantmanagementsystem.pos.controller;

import com.restaurantmanagementsystem.pos.db.LoginDao;
import com.restaurantmanagementsystem.pos.db.LoginDaoImpl;
import com.restaurantmanagementsystem.pos.model.UserAuthenticationResult;
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
import java.sql.SQLException;

public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Text loginMessageLabel;
    @FXML
    private Hyperlink createAccountLink;

    private LoginDao loginDao = new LoginDaoImpl();

    @FXML
    private void handleLoginButtonAction() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        UserAuthenticationResult result = loginDao.authenticate(username, password);
        if (result.isAuthenticated()) {
            loginMessageLabel.setText("Login successful.");
            loadDashboard(result.getUserRole(), username);
        } else {
            loginMessageLabel.setText("Login failed. Please try again.");
        }
    }

    @FXML
    private void handleSwitchToRegister() {
        try {
            Parent registerView = FXMLLoader.load(getClass().getResource("/com/restaurantmanagementsystem/pos/view/register.fxml"));
            Stage stage = (Stage) createAccountLink.getScene().getWindow();
            stage.setScene(new Scene(registerView));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            loginMessageLabel.setText("Failed to load the registration view.");
        }
    }

    private void loadDashboard(String role, String username) {
        try {
            FXMLLoader loader = new FXMLLoader();
            String fxmlPath;

            if ("admin".equals(role)) {
                fxmlPath = "/com/restaurantmanagementsystem/pos/view/admin.fxml";
            } else if ("customer".equals(role)) {
                fxmlPath = "/com/restaurantmanagementsystem/pos/view/customer.fxml";
            } else {
                // Handle error or throw exception if the role is neither admin nor customer
                loginMessageLabel.setText("Role not recognized.");
                return;
            }

            loader.setLocation(getClass().getResource(fxmlPath));
            Parent dashboardView = loader.load();

            if ("admin".equals(role)) {
                AdminController adminController = loader.getController();
                adminController.setUsername(username);
            } else {
                CustomerController customerController = loader.getController();
                customerController.setUsername(username);
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
        // Log error and possibly inform the user
        System.err.println("SQLState: " + ex.getSQLState());
        System.err.println("Error Code: " + ex.getErrorCode());
        System.err.println("Message: " + ex.getMessage());
        ex.printStackTrace();
        // Show error message to the user
        loginMessageLabel.setText("A database error occurred. Please try again.");
    }
}
