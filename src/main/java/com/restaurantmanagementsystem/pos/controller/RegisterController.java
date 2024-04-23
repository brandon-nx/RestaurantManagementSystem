package com.restaurantmanagementsystem.pos.controller;

import com.restaurantmanagementsystem.pos.db.RegisterDao;
import com.restaurantmanagementsystem.pos.db.RegisterDaoImpl;
import com.restaurantmanagementsystem.pos.model.AlertUtils;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class RegisterController {

    @FXML
    private TextField usernameField, contactField, addressField;
    @FXML
    private PasswordField passwordField, confirmPasswordField;

    private RegisterDao registerDao = new RegisterDaoImpl();

    @FXML
    private void handleSwitchToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/restaurantmanagementsystem/pos/view/login.fxml"));
            Parent loginView = loader.load();

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(loginView));
            stage.show();
        } catch (IOException e) {
            AlertUtils.showErrorAlert("Load Error", "Failed to load the login view.");
        }
    }

    @FXML
    private void handleRegisterButtonAction() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();
        String contact = contactField.getText().trim();
        String address = addressField.getText().trim();


        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || contact.isEmpty() || address.isEmpty()) {
            AlertUtils.showErrorAlert("Registration Error", "Please complete all fields.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            AlertUtils.showErrorAlert("Password Mismatch", "Passwords do not match. Please try again.");
            return;
        }

        if (!contact.matches("\\d{10}")) {
            AlertUtils.showErrorAlert("Invalid Contact", "Please enter a valid 10-digit contact number.");
            return;
        }

        if (registerCustomer(username, password, contact, address)) {
            AlertUtils.showInformationAlert("Registration Successful", "You have been registered successfully!");
            handleSwitchToLogin();
        }
    }

    private boolean registerCustomer(String username, String password, String contact, String address) {
        if (!registerDao.checkUsernameExists(username)) {
            return registerDao.registerCustomer(username, password, contact, address);
        } else {
            AlertUtils.showErrorAlert("Registration Error", "Username already exists.");
            return false;
        }
    }
}
