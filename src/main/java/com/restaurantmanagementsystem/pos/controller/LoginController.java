package com.restaurantmanagementsystem.pos.controller;

import com.restaurantmanagementsystem.pos.db.LoginDao;
import com.restaurantmanagementsystem.pos.db.LoginDaoImpl;
import com.restaurantmanagementsystem.pos.model.AlertUtils;
import com.restaurantmanagementsystem.pos.model.User;
import com.restaurantmanagementsystem.pos.model.UserAuthenticationResult;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
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
    private Hyperlink createAccountLink;

    private final LoginDao loginDao = new LoginDaoImpl();

    @FXML
    private void handleSwitchToRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/restaurantmanagementsystem/pos/view/register.fxml"));
            Parent loginView = loader.load();

            Stage stage = (Stage) createAccountLink.getScene().getWindow();
            stage.setScene(new Scene(loginView));
            stage.show();
        } catch (IOException e) {
            AlertUtils.showErrorAlert("Load Error", "Failed to load the register view.");
        }
    }

    @FXML
    private void handleLoginButtonAction() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        try {
            UserAuthenticationResult result = loginDao.authenticate(username, password);
            if (result.isAuthenticated()) {
                AlertUtils.showInformationAlert("Login Successful", "You have successfully logged in.");
                loadDashboard(result.getUserRole(), result.getUserId(), username);
            } else {
                AlertUtils.showErrorAlert("Login Failed", "Login failed. Please try again.");
            }
        } catch (IOException e) {
            AlertUtils.showErrorAlert("Load Error", "Failed to load the dashboard.");
        }
    }

    private void loadDashboard(String role, String userId, String username) throws IOException {
        String fxmlPath = determineFxmlPath(role);
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent dashboardView = loader.load();
        passUserInfoToController(loader.getController(), userId, username, role);
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.setScene(new Scene(dashboardView));
        stage.show();
    }

    private String determineFxmlPath(String role) {
        String fxmlPath;
        if ("admin".equals(role)) {
            fxmlPath = "/com/restaurantmanagementsystem/pos/view/admin.fxml";
        } else if ("customer".equals(role)) {
            fxmlPath = "/com/restaurantmanagementsystem/pos/view/customer.fxml";
        } else {
            throw new IllegalArgumentException("Role not recognized: " + role);
        }
        return fxmlPath;
    }

    private void passUserInfoToController(Object controller, String userId, String username, String role) {
        if (controller instanceof AdminController adminController) {
            adminController.setUser(new User(userId, username, null, role, null, null));
        } else if (controller instanceof CustomerController customerController) {
            customerController.setUser(new User(userId, username, null, role, null, null));
        }
    }
}
