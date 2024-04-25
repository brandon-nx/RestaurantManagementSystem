package com.restaurantmanagementsystem.pos.controller;

import com.restaurantmanagementsystem.pos.model.AlertUtils;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class LoginControllerTest {

    private LoginController loginController;
    private Stage stage;

    @BeforeAll
    public static void setupJavaFXRuntime() {
        Platform.startup(() -> {});
    }

    @BeforeEach
    public void setUp() throws Exception {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/restaurantmanagementsystem/pos/view/login.fxml"));
                Parent root = loader.load();
                loginController = loader.getController();
                stage = new Stage();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (Exception e) {
                AlertUtils.showErrorAlert("Unexpected error", "An unexpected error has occurred. " + e.getMessage());
            }
        });

        Thread.sleep(1000);
    }

    @Test
    public void testHandleLoginWithAdminCredentials() throws Exception {
        Platform.runLater(() -> {
            loginController.usernameField.setText("adminUser");
            loginController.passwordField.setText("adminPass");

            loginController.handleLoginButtonAction();

            assertTrue(stage.getScene().getRoot().getId().contains("admin.fxml"),
                    "Admin dashboard should be loaded for admin role");
        });

        Thread.sleep(500);
    }

    @Test
    public void testHandleLoginWithCustomerCredentials() throws Exception {
        Platform.runLater(() -> {
            loginController.usernameField.setText("customerUser");
            loginController.passwordField.setText("customerPass");

            loginController.handleLoginButtonAction();

            assertTrue(stage.getScene().getRoot().getId().contains("customer.fxml"),
                    "Customer dashboard should be loaded for customer role");
        });

        Thread.sleep(500);
    }

    @Test
    public void testHandleSwitchToRegister() throws Exception {
        Platform.runLater(() -> {
            loginController.handleSwitchToRegister();

            String loadedRootId = stage.getScene().getRoot().getId();
            String expectedRootId = "registerView";

            assertEquals(expectedRootId, loadedRootId,
                    "The scene should switch to the register view FXML.");
        });

        Thread.sleep(500);
    }

    @AfterEach
    public void tearDown() {
        Platform.runLater(() -> stage.close());
    }
}
