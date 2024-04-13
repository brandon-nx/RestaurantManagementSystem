package com.restaurantmanagementsystem.pos.controller;

import com.restaurantmanagementsystem.pos.db.MenuDao;
import com.restaurantmanagementsystem.pos.db.MenuDaoImpl;
import com.restaurantmanagementsystem.pos.model.MenuItem;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import java.io.File;
import java.util.List;
import java.util.Optional;

public class InventoryController {
    @FXML
    public TableColumn productIdColumn, productNameColumn, categoryColumn, priceColumn, statusColumn, stockColumn, imageColumn;
    @FXML
    private TableColumn<MenuItem, Void> deleteColumn;
    @FXML
    public TextField productNameField, stockField, priceField;
    @FXML
    public ComboBox<String> typeComboBox, statusComboBox;
    @FXML
    private ImageView productImageView;
    @FXML
    private TableView<MenuItem> productTable;
    private MenuDao menuDao = new MenuDaoImpl();
    private String currentImagePath;

    public void initialize() {
        // Set up the ComboBox for types
        typeComboBox.setItems(FXCollections.observableArrayList(
                "Appetizers", "Entrées", "Sides", "Desserts", "Beverages"
        ));

        // Set up the ComboBox for status
        statusComboBox.setItems(FXCollections.observableArrayList(
                "Available", "Unavailable"
        ));

        handleProductTableView();
        setupDeleteColumn();

        productTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                fillInputFieldsWithSelectedItemDetails(newSelection);
            }
        });
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

    private void setupDeleteColumn() {
        deleteColumn.setCellFactory(param -> new TableCell<MenuItem, Void>() {
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setOnAction((ActionEvent event) -> {
                    MenuItem item = getTableView().getItems().get(getIndex());
                    handleDeleteAction(item);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteButton);
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

    // Add Product Button
    @FXML
    private void handleAddAction() {
        // Check if any field is empty
        if (productNameField.getText().trim().isEmpty() ||
                stockField.getText().trim().isEmpty() ||
                priceField.getText().trim().isEmpty() ||
                typeComboBox.getSelectionModel().isEmpty() ||
                statusComboBox.getSelectionModel().isEmpty() ||
                currentImagePath == null || currentImagePath.isEmpty()) {

            showErrorAlert("Input Error", "Please fill in all the fields and import an image.");
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
            showErrorAlert("Input Error", "Please enter a valid number for stock.");
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceField.getText().trim());
            price = Math.round(price * 100.0) / 100.0; // Ensures the price is with two decimal places
        } catch (NumberFormatException e) {
            showErrorAlert("Input Error", "Please enter a valid price.");
            return;
        }

        MenuItem newItem = new MenuItem(productId, name, price, currentImagePath, category, stock, status);
        boolean success = menuDao.addMenuItems(newItem);

        if (success) {
            handleClearAction();
            loadProducts();
        } else {
            showErrorAlert("Add Item Failed", "Could not add the new menu item to the database.");
        }
    }

    private String generateProductId() {
        int lastIdNumber = menuDao.getLastIdNumber(); // This method should return the numeric part of the last ID
        int nextIdNumber = lastIdNumber + 1;
        return String.format("P-%03d", nextIdNumber);
    }

    // Delete Product Button
    @FXML
    private void handleDeleteAction(MenuItem itemToDelete) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText("Delete Item");
        confirmAlert.setContentText("Are you sure you want to delete this item?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // If user confirmed, proceed with deletion
            boolean success = menuDao.deleteMenuItem(itemToDelete);
            if (success) {
                // If successful, remove the item from the TableView
                productTable.getItems().remove(itemToDelete);
                showInformationAlert("Item Deleted", "The item was successfully deleted.");
            } else {
                // If unsuccessful, show an error message
                showErrorAlert("Deletion Failed", "Could not delete the item from the database.");
            }
        }
    }

    @FXML
    private void handleUpdateAction() {
        // Get the selected product
        MenuItem selectedItem = productTable.getSelectionModel().getSelectedItem();

        // Check if a product is actually selected
        if (selectedItem == null) {
            showErrorAlert("Update Error", "No product selected for update.");
            return;
        }

        // Check if all fields are filled
//        if (productNameField.getText().trim().isEmpty() ||
//                stockField.getText().trim().isEmpty() ||
//                priceField.getText().trim().isEmpty() ||
//                typeComboBox.getSelectionModel().isEmpty() ||
//                statusComboBox.getSelectionModel().isEmpty() ||
//                currentImagePath == null || currentImagePath.isEmpty()) {
//
//            showErrorAlert("Input Error", "Please fill in all the fields and import an image.");
//            return;
//        }

        // Check if any information has changed
        if (productNameField.getText().trim().equals(selectedItem.getName()) &&
                stockField.getText().trim().equals(String.valueOf(selectedItem.getStock())) &&
                priceField.getText().trim().equals(String.format("%.2f", selectedItem.getPrice())) &&
                typeComboBox.getValue().equals(selectedItem.getCategory()) &&
                statusComboBox.getValue().equals(selectedItem.getStatus()) &&
                currentImagePath.equals(selectedItem.getImagePath())) {

            showInformationAlert("No Change", "No information has changed.");
            return;
        }

        // Confirm the update action with the admin
        Alert confirmUpdateAlert = new Alert(Alert.AlertType.CONFIRMATION, "Confirm Update", ButtonType.YES, ButtonType.CANCEL);
        confirmUpdateAlert.setHeaderText("Update Product");
        confirmUpdateAlert.setContentText("Are you sure you want to update the selected product?");

        Optional<ButtonType> result = confirmUpdateAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            // Parse and validate the new values
            try {
                int newStock = Integer.parseInt(stockField.getText().trim());
                double newPrice = Double.parseDouble(priceField.getText().trim());
                newPrice = Math.round(newPrice * 100.0) / 100.0;

                // Set the new values to the selected product
                selectedItem.setName(productNameField.getText().trim());
                selectedItem.setCategory(typeComboBox.getSelectionModel().getSelectedItem());
                selectedItem.setStatus(statusComboBox.getSelectionModel().getSelectedItem());
                selectedItem.setStock(newStock);
                selectedItem.setPrice(newPrice);
                selectedItem.setImagePath(currentImagePath);

                // Perform the update operation on the database
                boolean updateSuccess = menuDao.updateMenuItem(selectedItem);

                // Check if the update was successful
                if (updateSuccess) {
                    productTable.refresh();
                    showInformationAlert("Update Successful", "The product details have been updated.");
                    handleClearAction();
                } else {
                    showErrorAlert("Update Failed", "Failed to update the product details.");
                }

            } catch (NumberFormatException e) {
                showErrorAlert("Input Error", "Please enter valid numbers for stock and price.");
            }
        }
    }

    @FXML
    private void handleClearAction() {
        productNameField.clear();
        priceField.clear();
        stockField.clear();
        typeComboBox.getSelectionModel().clearSelection();
        statusComboBox.getSelectionModel().clearSelection();
        productImageView.setImage(null);
        productTable.getSelectionModel().clearSelection();
        currentImagePath = null;
    }

    @FXML
    private void handleImportAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            Image image = new Image(file.toURI().toString());
            productImageView.setImage(image);
            currentImagePath = file.toURI().toString();
        }
    }

    private void fillInputFieldsWithSelectedItemDetails(MenuItem item) {
        productNameField.setText(item.getName());
        stockField.setText(String.valueOf(item.getStock()));
        priceField.setText(String.format("%.2f", item.getPrice()));
        typeComboBox.setValue(item.getCategory());
        statusComboBox.setValue(item.getStatus());

        Image image = new Image(item.getImagePath());
        productImageView.setImage(image);
        currentImagePath = item.getImagePath();
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInformationAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
