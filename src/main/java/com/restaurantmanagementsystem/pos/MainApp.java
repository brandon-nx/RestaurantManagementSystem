package com.restaurantmanagementsystem.pos;

import com.restaurantmanagementsystem.pos.model.AlertUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/restaurantmanagementsystem/pos/view/admin.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            primaryStage.setTitle("Restaurant Management System");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            AlertUtils.showErrorAlert("Failed to load the initial view.", "Please check that the file exists and is not corrupted: " + e.getMessage());
        } catch (IllegalStateException e) {
            AlertUtils.showErrorAlert("Application cannot be launched.", "There is a problem with the application's setup: " + e.getMessage());
        } catch (Exception e) {
            AlertUtils.showErrorAlert("Unexpected error", "An unexpected error has occurred. Please contact support: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}