package com.restaurantmanagementsystem.pos.controller;

import com.restaurantmanagementsystem.pos.model.AlertUtils;
import com.restaurantmanagementsystem.pos.model.Order;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrdersControllerTest {

    private OrdersController ordersController;
    private Stage stage;

    @BeforeAll
    public static void setupJavaFXRuntime() {
        Platform.startup(() -> {});
    }

    @BeforeEach
    public void setUp() throws Exception {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/restaurantmanagementsystem/pos/view/orders.fxml"));
                Parent root = loader.load();
                ordersController = loader.getController();
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
    public void testUpdateOrderStatus() throws Exception {
        Platform.runLater(() -> {
            Order order = new Order();
            order.setStatus("pending");
            String newStatus = "done";

            ordersController.updateOrderStatus(order, newStatus);

            assertEquals(newStatus, order.getStatus());
        });

        Thread.sleep(1000);
    }

    @AfterEach
    public void tearDown() throws Exception {
        Platform.runLater(() -> stage.close());
    }
}