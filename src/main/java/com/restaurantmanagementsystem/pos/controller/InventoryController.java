package com.restaurantmanagementsystem.pos.controller;

import com.restaurantmanagementsystem.pos.db.MenuDao;
import com.restaurantmanagementsystem.pos.db.MenuDaoImpl;
import com.restaurantmanagementsystem.pos.model.AlertUtils;
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

public class InventoryController {
    @FXML
    private TableView<MenuItem> productTable;
    @FXML
    private TableColumn<MenuItem, String> productIdColumn, productNameColumn, categoryColumn, statusColumn;
    @FXML
    private TableColumn<MenuItem, Double> priceColumn;
    @FXML
    private TableColumn<MenuItem, Integer> stockColumn;
    @FXML
    private TableColumn<MenuItem, Void> deleteColumn;
    @FXML
    private TableColumn<MenuItem, String> imageColumn;
    @FXML
    public TextField productNameField, stockField, priceField;
    @FXML
    public ComboBox<String> typeComboBox, statusComboBox;
    @FXML
    private ImageView productImageView;

    private MenuDao menuDao = new MenuDaoImpl();
    private String currentImagePath;

    private static final List<String> VALID_CATEGORIES = Arrays.asList("Appetizers", "Entrées", "Sides", "Desserts", "Beverages");
    private static final List<String> VALID_STATUSES = Arrays.asList("available", "unavailable");

    public void initialize() {
        setupComboBoxes();
        setupSelectionModel();
        setupProductTableView();
    }

    private void setupComboBoxes() {
        typeComboBox.setItems(FXCollections.observableArrayList(VALID_CATEGORIES));
        statusComboBox.setItems(FXCollections.observableArrayList(VALID_STATUSES));
    }

    private void setupSelectionModel() {
        productTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                fillInputFieldsWithSelectedItemDetails(newSelection);
            }
        });
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

    private void setupProductTableView() {
        productIdColumn.setCellValueFactory(new PropertyValueFactory<>("productId"));
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        setupImageColumn();
        setupDeleteColumn();
        loadProducts();
    }

    private void setupImageColumn() {
        imageColumn.setCellValueFactory(new PropertyValueFactory<>("imagePath"));
        imageColumn.setCellFactory(param -> new TableCell<MenuItem, String>() {
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
                    imageView.setImage(new Image(item, true));
                    setGraphic(imageView);
                }
                setAlignment(Pos.CENTER);
            }
        });
    }

    private void setupDeleteColumn() {
        deleteColumn.setCellFactory(param -> new TableCell<MenuItem, Void>() {
            private final Button deleteButton = createDeleteButton(this);

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });
    }

    private Button createDeleteButton(TableCell<MenuItem, Void> cell) {
        Button button = new Button("Delete");
        button.setOnAction(event -> handleDeleteAction(cell));
        return button;
    }




    // Add product action
    @FXML
    private void handleAddAction() {
        if (!validateProductInputs()) {
            return;
        }

        MenuItem newItem = createMenuItemFromInputs();
        boolean success = menuDao.addMenuItems(newItem);

        if (success) {
            handleClearAction();
            loadProducts();
            AlertUtils.showInformationAlert("Success", "Product added successfully.");
        } else {
            AlertUtils.showErrorAlert("Add Item Failed", "Could not add the new menu item to the database.");
        }
    }

    private MenuItem createMenuItemFromInputs() {
        String name = productNameField.getText().trim();
        String category = typeComboBox.getValue();
        String status = statusComboBox.getValue();
        int stock = Integer.parseInt(stockField.getText().trim());
        double price = Double.parseDouble(priceField.getText().trim());

        return new MenuItem(null, name, price, currentImagePath, category, stock, status);
    }

    // Delete product action
    private void handleDeleteAction(TableCell<MenuItem, Void> cell) {
        MenuItem item = cell.getTableView().getItems().get(cell.getIndex());
        if (item == null) return;
        if (AlertUtils.showConfirmationAlert("Confirm Deletion", "Are you sure you want to delete this item?")) {
            deleteMenuItem(item);
        }
    }

    private void deleteMenuItem(MenuItem item) {
        try {
            if (menuDao.deleteMenuItem(item)) {
                productTable.getItems().remove(item);
                AlertUtils.showInformationAlert("Item Deleted", "The item was successfully deleted.");
            } else {
                AlertUtils.showErrorAlert("Deletion Failed", "Could not delete the item from the database.");
            }
        } catch (SQLException e) {
            AlertUtils.showErrorAlert("Delete Error", e.getMessage());
        }
    }

    private void loadProducts() {
        productTable.getItems().clear();
        List<MenuItem> products = menuDao.getMenuItems();
        productTable.setItems(FXCollections.observableArrayList(products));
    }

    // Update product action
    @FXML
    private void handleUpdateAction() {
        MenuItem selectedItem = productTable.getSelectionModel().getSelectedItem();

        if (!isProductSelected(selectedItem)) return;
        if (!isInformationChanged(selectedItem)) {
            AlertUtils.showInformationAlert("No Change", "No information has changed.");
            return;
        }
        if (!validateProductInputs()) return;

        if ( AlertUtils.showConfirmationAlert("Confirm Update", "Are you sure you want to update the selected product?")) {
            try {
                int newStock = Integer.parseInt(stockField.getText().trim());
                double newPrice = Double.parseDouble(priceField.getText().trim());
                newPrice = Math.round(newPrice * 100.0) / 100.0;

                selectedItem.setName(productNameField.getText().trim());
                selectedItem.setCategory(typeComboBox.getValue());
                selectedItem.setStatus(statusComboBox.getValue());
                selectedItem.setStock(newStock);
                selectedItem.setPrice(newPrice);
                selectedItem.setImagePath(currentImagePath);

                boolean updateSuccess = menuDao.updateMenuItem(selectedItem);

                if (updateSuccess) {
                    productTable.refresh();
                    AlertUtils.showInformationAlert("Update Successful", "The product details have been updated.");
                    handleClearAction();
                } else {
                    AlertUtils.showErrorAlert("Update Failed", "Failed to update the product details.");
                }

            } catch (NumberFormatException e) {
                AlertUtils.showErrorAlert("Input Error", "Please enter valid numbers for stock and price.");
            }
        }
    }

    // Clear field action
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

    // Import image action
    @FXML
    private void handleImportAction() {
        File file = chooseFile();
        if (file != null) {
            setImageForProduct(file.toURI().toString());
        }
    }

    private File chooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        return fileChooser.showOpenDialog(null);
    }

    private void setImageForProduct(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            productImageView.setImage(null);
        } else {
            Image image = new Image(imagePath);
            productImageView.setImage(image);
        }
        currentImagePath = imagePath;
    }







    // Check validations logic
    private boolean validateProductInputs() {
        if (fieldsAreEmpty()) {
            AlertUtils.showErrorAlert("Input Error", "Please fill in all fields and import an image.");
            return false;
        }
        if (!isValidNumber(stockField.getText().trim(), "stock") ||
                !isValidNumber(priceField.getText().trim(), "price")) {
            return false;
        }
        if (!isValidSelection(typeComboBox, VALID_CATEGORIES, "category") ||
                !isValidSelection(statusComboBox, VALID_STATUSES, "status")) {
            return false;
        }
        return true;
    }

    private boolean fieldsAreEmpty() {
        return (productNameField.getText().trim().isEmpty() ||
                stockField.getText().trim().isEmpty() ||
                priceField.getText().trim().isEmpty() ||
                typeComboBox.getSelectionModel().isEmpty() ||
                statusComboBox.getSelectionModel().isEmpty() ||
                (currentImagePath == null || currentImagePath.isEmpty()));
    }

    boolean isValidNumber(String numberStr, String fieldName) {
        try {
            double number = Double.parseDouble(numberStr);
            return true;
        } catch (NumberFormatException e) {
            AlertUtils.showErrorAlert("Input Error", "Please enter a valid number for " + fieldName + ".");
            return false;
        }
    }

    private boolean isValidSelection(ComboBox<String> comboBox, List<String> validOptions, String fieldName) {
        String selection = comboBox.getValue();
        if (selection == null || !validOptions.contains(selection)) {
            AlertUtils.showErrorAlert("Input Error", "Please select a valid " + fieldName + ".");
            return false;
        }
        return true;
    }

    private boolean isProductSelected(MenuItem selectedItem) {
        if (selectedItem == null) {
            AlertUtils.showErrorAlert("Update Error", "No product selected for update.");
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
}
