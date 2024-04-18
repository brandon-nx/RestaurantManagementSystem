package com.restaurantmanagementsystem.pos.controller;

import com.restaurantmanagementsystem.pos.db.LoginDao;
import com.restaurantmanagementsystem.pos.db.LoginDaoImpl;
import com.restaurantmanagementsystem.pos.model.User;
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
            // Assuming authenticate method has been updated to return User object as part of the result
            User loggedInUser = new User(
                    result.getUserId(),
                    username,
                    null, // Consider not storing the password in memory
                    result.getUserRole(),
                    null, // Contact might not be available at this stage
                    null  // Address might not be available at this stage
            );
            loginMessageLabel.setText("Login successful.");
            loadDashboard(loggedInUser);
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

    private void loadDashboard(User user) {
        try {
            String fxmlPath = "";
            if ("admin".equals(user.getRole())) {
                fxmlPath = "/com/restaurantmanagementsystem/pos/view/admin.fxml";
            } else if ("customer".equals(user.getRole())) {
                fxmlPath = "/com/restaurantmanagementsystem/pos/view/customer.fxml";
            } else {
                loginMessageLabel.setText("Role not recognized.");
                return;
            }

            // Now the FXMLLoader is initialized with the location of the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent dashboardView = loader.load();

            // After the FXML file has been loaded, get the controller and pass the user
            if ("admin".equals(user.getRole())) {
                AdminController adminController = loader.getController();
                adminController.setUser(user);
            } else if ("customer".equals(user.getRole())) {
                CustomerController customerController = loader.getController();
                customerController.setUser(user);
            }

            // Show the dashboard
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(dashboardView));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            loginMessageLabel.setText("Failed to load the dashboard.");
        }
    }
}
