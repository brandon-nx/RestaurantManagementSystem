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
import java.sql.SQLException;
import java.util.Arrays;
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
    private static final List<String> VALID_CATEGORIES = Arrays.asList("Appetizers", "Entrées", "Sides", "Desserts", "Beverages");
    private static final List<String> VALID_STATUSES = Arrays.asList("available", "unavailable");



    public void initialize() {
        setupComboBoxes();
        setupProductTableView();
        setupDeleteColumn();
        setupSelectionModel();
    }

    private void setupComboBoxes() {
        typeComboBox.setItems(FXCollections.observableArrayList(VALID_CATEGORIES));
        statusComboBox.setItems(FXCollections.observableArrayList(VALID_STATUSES));
    }

    private void setupProductTableView() {
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

    private void setupSelectionModel() {
        productTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                fillInputFieldsWithSelectedItemDetails(newSelection);
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
        // Check if all fields are filled
        if (!validateProductInputs()) {
            return;
        }

        // Proceed add item
        MenuItem newItem = createMenuItemFromInputs();
        boolean success = menuDao.addMenuItems(newItem);

        if (success) {
            handleClearAction();
            loadProducts();
            showInformationAlert("Success", "Product added successfully.");
        } else {
            showErrorAlert("Add Item Failed", "Could not add the new menu item to the database.");
        }
    }

    // Delete Product Button
    public void handleDeleteAction(MenuItem itemToDelete) {
        try {
            if (menuDao.deleteMenuItem(itemToDelete)) {
                if (showConfirmationAlert("Confirm Deletion", "Are you sure you want to delete this item?")) {
                    boolean success = menuDao.deleteMenuItem(itemToDelete);
                    if (success) {
                        productTable.getItems().remove(itemToDelete);
                        showInformationAlert("Item Deleted", "The item was successfully deleted.");
                    } else {
                        showErrorAlert("Deletion Failed", "Could not delete the item from the database.");
                    }
                }
            }
        } catch (SQLException e) {
            showErrorAlert("Delete Error", e.getMessage());
        }
    }

    @FXML
    private void handleUpdateAction() {
        // Get the selected product
        MenuItem selectedItem = productTable.getSelectionModel().getSelectedItem();

        // Check if a product is selected
        if (!isProductSelected(selectedItem)) return;

        // Check if any information has changed
        if (!isInformationChanged(selectedItem)) {
            showInformationAlert("No Change", "No information has changed.");
            return;
        }

        // Check if all fields are filled
        if (!validateProductInputs()) return;

        // Proceed update item
        if (showConfirmationAlert("Confirm Update", "Are you sure you want to update the selected product?")) {
            try {
                int newStock = Integer.parseInt(stockField.getText().trim());
                double newPrice = Double.parseDouble(priceField.getText().trim());
                newPrice = Math.round(newPrice * 100.0) / 100.0;

                // Set the new values to the selected product
                selectedItem.setName(productNameField.getText().trim());
                selectedItem.setCategory(typeComboBox.getValue());
                selectedItem.setStatus(statusComboBox.getValue());
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

    private MenuItem createMenuItemFromInputs() {
        String productId = generateProductId();
        String name = productNameField.getText().trim();
        String category = typeComboBox.getValue();
        String status = statusComboBox.getValue();
        int stock = Integer.parseInt(stockField.getText().trim());
        double price = Double.parseDouble(priceField.getText().trim());

        return new MenuItem(productId, name, price, currentImagePath, category, stock, status);
    }

    private boolean validateProductInputs() {
        // Check if any field is empty
        if (productNameField.getText().trim().isEmpty() ||
                stockField.getText().trim().isEmpty() ||
                priceField.getText().trim().isEmpty() ||
                typeComboBox.getSelectionModel().isEmpty() ||
                statusComboBox.getSelectionModel().isEmpty() ||
                currentImagePath == null || currentImagePath.isEmpty()) {
            showErrorAlert("Input Error", "Please fill in all the fields and import an image.");
            return false;
        }

        try {
            int stock = Integer.parseInt(stockField.getText().trim());
        } catch (NumberFormatException e) {
            showErrorAlert("Input Error", "Please enter a valid number for stock.");
            return false;
        }

        try {
            double price = Double.parseDouble(priceField.getText().trim());
            price = Math.round(price * 100.0) / 100.0;
        } catch (NumberFormatException e) {
            showErrorAlert("Input Error", "Please enter a valid price.");
            return false;
        }

        String selectedCategory = typeComboBox.getValue();
        if (selectedCategory == null || !VALID_CATEGORIES.contains(selectedCategory)) {
            showErrorAlert("Input Error", "Please select a valid category.");
            return false;
        }

        // Validate the selected status
        String selectedStatus = statusComboBox.getValue();
        if (selectedStatus == null || !VALID_STATUSES.contains(selectedStatus)) {
            showErrorAlert("Input Error", "Please select a valid status.");
            return false;
        }

        // If all checks pass, return true
        return true;
    }

    private String generateProductId() {
        int lastIdNumber = menuDao.getLastIdNumber();
        int nextIdNumber = lastIdNumber + 1;
        return String.format("P-%03d", nextIdNumber);
    }

    private boolean isProductSelected(MenuItem selectedItem) {
        if (selectedItem == null) {
            showErrorAlert("Update Error", "No product selected for update.");
            return false;
        }
        return true;
    }

    private boolean isInformationChanged(MenuItem selectedItem) {
        boolean nameChanged = !productNameField.getText().trim().equals(selectedItem.getName());
        boolean stockChanged = !stockField.getText().trim().equals(String.valueOf(selectedItem.getStock()));
        boolean priceChanged = !priceField.getText().trim().equals(String.format("%.2f", selectedItem.getPrice()));
        boolean categoryChanged = !typeComboBox.getValue().equals(selectedItem.getCategory());
        boolean statusChanged = !statusComboBox.getValue().equals(selectedItem.getStatus());
        boolean imageChanged = currentImagePath != null && !currentImagePath.equals(selectedItem.getImagePath());

        return nameChanged || stockChanged || priceChanged || categoryChanged || statusChanged || imageChanged;
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

    private boolean showConfirmationAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}
