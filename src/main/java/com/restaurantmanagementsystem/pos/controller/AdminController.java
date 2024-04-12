package com.restaurantmanagementsystem.pos.controller;

import com.restaurantmanagementsystem.pos.db.MenuDao;
import com.restaurantmanagementsystem.pos.db.MenuDaoImpl;
import com.restaurantmanagementsystem.pos.model.MenuItem;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class AdminController {
    @FXML
    public TableColumn productIdColumn, productNameColumn, categoryColumn, priceColumn, statusColumn, stockColumn, imageColumn;
    public TextField productNameField, stockField, priceField;
    public ComboBox<String> typeComboBox, statusComboBox;
    @FXML
    private Button signOutButton, addButton, updateButton, clearButton, deleteButton, importButton;
    @FXML
    private ImageView productImageView;
    @FXML
    private Text welcomeText;
    @FXML
    private TableView<MenuItem> productTable;
    private MenuDao menuDao = new MenuDaoImpl();
    private String currentImagePath;

    public void initialize() {
        welcomeText.setText("Welcome, Admin111");

        // Set up the ComboBox for types
        typeComboBox.setItems(FXCollections.observableArrayList(
                "Appetizers", "Entrées", "Sides", "Desserts", "Beverages"
        ));

        // Set up the ComboBox for status
        statusComboBox.setItems(FXCollections.observableArrayList(
                "Available", "Unavailable"
        ));

        handleProductTableView();
    }

    public void setUsername(String username) {
        welcomeText.setText("Welcome, " + username);
    }

    // Load All Product Information
    private void handleProductTableView() {
        setupColumn(productIdColumn, "productId");
        setupColumn(productNameColumn, "name");
        setupColumn(priceColumn, "price");
        setupColumn(categoryColumn, "category");
        setupColumn(stockColumn, "stock");
        setupColumn(statusColumn, "status");
        setupImageColumn(imageColumn, "imagePath");
        loadProducts();
    }

    private <T> void setupColumn(TableColumn<MenuItem, T> column, String propertyName) {
        column.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        column.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (item instanceof Double) {
                    setText(empty ? null : String.format("%.2f", item));
                } else {
                    setText(empty ? null : String.valueOf(item));
                }
                setAlignment(Pos.CENTER);
            }
        });
    }

    private void setupImageColumn(TableColumn<MenuItem, String> column, String propertyName) {
        column.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        column.setCellFactory(param -> new TableCell<MenuItem, String>() {
            private final ImageView imageView = new ImageView();
            {
                imageView.setFitHeight(50);
                imageView.setFitWidth(50);
                imageView.setPreserveRatio(true);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.isEmpty()) {
                    setGraphic(null);
                } else {
                    Image image = new Image(item, true);
                    imageView.setImage(image);
                    setGraphic(imageView);
                }
                setAlignment(Pos.CENTER);
            }
        });
    }

    private void loadProducts() {
        // Clear existing items
        productTable.getItems().clear();

        // Fetch new list of products from the database
        List<MenuItem> products = menuDao.getMenuItems();

        // Update the table view with the new items
        productTable.setItems(FXCollections.observableArrayList(products));
    }


    @FXML
    private void handleSignOutAction() {
        // Create a confirmation dialog.
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Sign Out");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Are you sure you want to sign out?");

        // Show the alert and wait for the user's response.
        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // If the user confirms, sign out and load the login view.
            try {
                // Load the login screen.
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/restaurantmanagementsystem/pos/view/login.fxml")); // Correct the path if necessary.
                Parent loginView = loader.load();

                // Get the current stage (window) from any component.
                Stage stage = (Stage) signOutButton.getScene().getWindow();
                stage.setScene(new Scene(loginView));
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
                // Here, set a message or log the error as appropriate.
            }
        }
    }

    // Add Product Button
    @FXML
    private void handleAddAction(ActionEvent event) {
        // Check if any field is empty
        if (productNameField.getText().trim().isEmpty() ||
            stockField.getText().trim().isEmpty() ||
            priceField.getText().trim().isEmpty() ||
            typeComboBox.getSelectionModel().isEmpty() ||
            statusComboBox.getSelectionModel().isEmpty() ||
            currentImagePath == null || currentImagePath.isEmpty()) {

            showAlert("Input Error", "Please fill in all the fields and import an image.");
            return;
        }

        // Proceed with processing since all fields are filled
        String productId = generateProductId();
        String name = productNameField.getText().trim();
        String category = typeComboBox.getSelectionModel().getSelectedItem().toString();
        String status = statusComboBox.getSelectionModel().getSelectedItem().toString();

        // Parsing and validation of number fields
        int stock;
        try {
            stock = Integer.parseInt(stockField.getText().trim());
        } catch (NumberFormatException e) {
            showAlert("Input Error", "Please enter a valid number for stock.");
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceField.getText().trim());
            price = Math.round(price * 100.0) / 100.0; // Ensures the price is with two decimal places
        } catch (NumberFormatException e) {
            showAlert("Input Error", "Please enter a valid price.");
            return; // Exit early if input is not valid
        }

        MenuItem newItem = new MenuItem(productId, name, price, currentImagePath, category, stock, status);
        boolean success = menuDao.addMenuItems(newItem);

        if (success) {
            clearInputFields();
            loadProducts();
        } else {
            showAlert("Add Item Failed", "Could not add the new menu item to the database.");
        }
    }

    private String generateProductId() {
        int lastIdNumber = menuDao.getLastIdNumber(); // This method should return the numeric part of the last ID
        int nextIdNumber = lastIdNumber + 1;
        return String.format("P-%03d", nextIdNumber);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null); // No header
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearInputFields() {
        // Clear all input fields
        productNameField.clear();
        priceField.clear();
        stockField.clear();
        typeComboBox.getSelectionModel().clearSelection();
        statusComboBox.getSelectionModel().clearSelection();
        productImageView.setImage(null);
    }

    @FXML
    private void handleUpdateAction(ActionEvent event) {
        // Implement your logic for updating a product
    }

    @FXML
    private void handleClearAction(ActionEvent event) {
        // Implement your logic for clearing form fields
    }

    @FXML
    private void handleDeleteAction(ActionEvent event) {
        // Implement your logic for deleting a product
    }

    @FXML
    private void handleImportAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            Image image = new Image(file.toURI().toString());
            productImageView.setImage(image);
            currentImagePath = file.toURI().toString(); // Save the URI as a string
        }
    }
}
