package com.restaurantmanagementsystem.pos.controller;

import com.restaurantmanagementsystem.pos.db.MenuDao;
import com.restaurantmanagementsystem.pos.db.MenuDaoImpl;
import com.restaurantmanagementsystem.pos.model.OrderItem;
import com.restaurantmanagementsystem.pos.model.MenuItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CustomerController {
    public Button clearOrderButton;
    public Button confirmOrderButton;
    public Button receiptButton;
    public Button appetizersButton;
    public Button entreesButton;
    public Button sidesButton;
    public Button dessertsButton;
    public Button beveragesButton;
    @FXML
    private Text welcomeText;
    @FXML
    private Text totalText;
    @FXML
    private Button signOutButton;
    @FXML
    private TableView<OrderItem> orderDetailsTable;
    @FXML
    private TableColumn<OrderItem, String> productNameColumn;
    @FXML
    private TableColumn<OrderItem, Number> quantityColumn;
    @FXML
    private TableColumn<OrderItem, Number> priceColumn;
    private ObservableList<OrderItem> orderItems = FXCollections.observableArrayList();
    @FXML
    private VBox centerVBox;
    private MenuDao menuDao = new MenuDaoImpl();
    private final List<OrderItem> allConfirmedOrders = new ArrayList<>();


    @FXML
    public void initialize() {
        welcomeText.setText("Welcome, Customer");
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        orderDetailsTable.setItems(orderItems);
        handleRemoveItemAction();
        loadCategory("Appetizers");
    }

    public void setUsername(String username) {
        welcomeText.setText("Welcome, " + username);
    }

    // Sidebar menu with categories
    @FXML
    private void handleCategoryAction(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        String category = getButtonCategory(clickedButton);

        loadCategory(category);
    }

    private String getButtonCategory(Button button) {
        String buttonId = button.getId();
        return switch (buttonId) {
            case "appetizersButton" -> "Appetizers";
            case "entreesButton" -> "Entrées";
            case "sidesButton" -> "Sides";
            case "dessertsButton" -> "Desserts";
            case "beveragesButton" -> "Beverages";
            default -> ""; // Default case to handle any unexpected button ids
        };
    }

    private void loadCategory(String category) {
        List<MenuItem> filteredItems = menuDao.getMenuItemsByCategory(category);
        displayMenuItems(filteredItems);
    }

    // Display Menu Items
    private void displayMenuItems(List<MenuItem> menuItems) {
        centerVBox.getChildren().clear();
        HBox currentRow = new HBox(20);
        centerVBox.getChildren().add(currentRow);

        int counter = 0;
        for (MenuItem menuItem : menuItems) {
            if (counter == 4) {
                currentRow = new HBox(20);
                centerVBox.getChildren().add(currentRow);
                counter = 0;
            }

            VBox itemVBox = createMenuItemVBox(menuItem);
            currentRow.getChildren().add(itemVBox);

            Button addButton = (Button) itemVBox.lookup("#addButton" + menuItem.getProductId());
            if (menuItem.getStock() <= 0 && addButton != null) {
                addButton.setDisable(true);
                addButton.setTooltip(new Tooltip("Out of stock"));
            }

            counter++;
        }
    }



    private VBox createMenuItemVBox(MenuItem menuItem) {
        VBox itemVBox = new VBox(10);
        itemVBox.setAlignment(Pos.CENTER);
        ImageView imageView = createImageView(menuItem.getImagePath());
        Text itemName = new Text(menuItem.getName() + " RM" + menuItem.getPrice());
        Button addButton = new Button("Add");
        addButton.setId("addButton");
        addButton.setOnAction(this::handleAddItemAction);
        addButton.setUserData(new OrderItem(menuItem.getName(), 1, menuItem.getPrice()));

        if (menuItem.getStock() <= 0) {
            addButton.setDisable(true);
            addButton.setTooltip(new Tooltip("Out of stock"));
        }

        itemVBox.getChildren().addAll(imageView, itemName, addButton);
        return itemVBox;
    }

    private ImageView createImageView(String imagePath) {
        ImageView imageView = null;
        try {
            Image image;
            if (!imagePath.startsWith("http") && !imagePath.startsWith("file:")) {
                URL imageUrl = getClass().getResource(imagePath);
                if (imageUrl != null) {
                    image = new Image(imageUrl.toExternalForm());
                } else {
                    throw new IllegalArgumentException("Image not found: " + imagePath);
                }
            } else {
                image = new Image(imagePath);
            }
            imageView = new ImageView(image);
            imageView.setFitHeight(100);
            imageView.setFitWidth(150);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
        } catch (Exception e) {
            e.printStackTrace();
            imageView = new ImageView(new Image("logo.png"));
            imageView.setFitHeight(100);
            imageView.setFitWidth(150);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
        }
        return imageView;
    }

    // Sign Out Button
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

    // Add Item Button
    @FXML
    private void handleAddItemAction(ActionEvent event) {
        Button addButton = (Button) event.getSource();
        OrderItem orderItem = (OrderItem) addButton.getUserData();
        MenuItem menuItem = menuDao.getMenuItemsByName(orderItem.getProductName()); // Note the method name changed to getMenuItemByName

        if (menuItem != null) {
            if (menuItem.getStock() > 0) {
                // Here, you should check not just the stock is greater than 0,
                // but also that it can cover the quantity being added to the order
                addOrUpdateOrderItem(orderItem, menuItem);
            } else {
                // Show error message if stock is not sufficient
                showErrorAlert("Out of Stock", menuItem.getName() + " is out of stock!");
            }
        } else {
            // Show error message if item is not found
            showErrorAlert("Item Not Found", "Could not find " + orderItem.getProductName() + " in the menu.");
        }
    }

    // Helper method to add or update an OrderItem in the order
    private void addOrUpdateOrderItem(OrderItem itemToAdd, MenuItem menuItem) {
        Optional<OrderItem> existingOrderItem = orderItems.stream()
                .filter(orderItem -> orderItem.getProductName().equals(itemToAdd.getProductName()))
                .findFirst();

        if (existingOrderItem.isPresent()) {
            // If the item already exists in the order, increase the quantity
            OrderItem existingItem = existingOrderItem.get();
            if (existingItem.getQuantity() + 1 <= menuItem.getStock()) {
                existingItem.setQuantity(existingItem.getQuantity() + 1);
            } else {
                // Show error message if the stock is not enough for adding one more item
                showErrorAlert("Stock Error", "Not enough stock for " + itemToAdd.getProductName());
                return;
            }
        } else {
            // If the item is new to the order and there is stock, add the new item with quantity 1
            if (menuItem.getStock() > 0) {
                orderItems.add(new OrderItem(itemToAdd.getProductName(), 1, itemToAdd.getPrice()));
            } else {
                showErrorAlert("Stock Error", "Not enough stock for " + itemToAdd.getProductName());
                return;
            }
        }

        orderDetailsTable.refresh(); // Refresh the TableView to show updated quantities
        calculateTotal(); // Recalculate the total price
    }

    // Remove Button
    private void handleRemoveItemAction() {
        TableColumn<OrderItem, Void> actionCol = new TableColumn<>("Actions");

        Callback<TableColumn<OrderItem, Void>, TableCell<OrderItem, Void>> cellFactory = param -> new TableCell<>() {
            private final Button btnRemove = new Button("Remove");

            {
                btnRemove.setOnAction(event -> {
                    OrderItem item = getTableView().getItems().get(getIndex());
                    orderItems.remove(item);
                    calculateTotal();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnRemove);
                }
            }
        };

        actionCol.setCellFactory(cellFactory);

        orderDetailsTable.getColumns().add(actionCol);
    }

    // Clear Order Button
    @FXML
    private void handleClearOrderAction() {
        if (orderItems.isEmpty()) {
            Alert emptyOrderAlert = new Alert(Alert.AlertType.WARNING, "No items in the order. Please add food to your order before confirming.");
            emptyOrderAlert.setHeaderText("Empty Order");
            emptyOrderAlert.showAndWait();
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION, "Clear the entire order?", ButtonType.YES, ButtonType.NO);
        confirmAlert.setHeaderText(null);
        Optional<ButtonType> result = confirmAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            // Clear the entire order
            orderItems.clear();
            // Refresh the TableView
            orderDetailsTable.refresh();
            // Reset the total amount
            calculateTotal();
        }
    }

    // Confirm Order Button
    @FXML
    private void handleConfirmOrderAction() {
        if (orderItems.isEmpty()) {
            showErrorAlert("Empty Order", "No items in the order.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to confirm the order?");
        confirmAlert.setTitle("Confirm Order");
        confirmAlert.setHeaderText(null);

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean isStockSufficient = true;

            // Check for stock availability before confirming the order
            for (OrderItem orderItem : orderItems) {
                MenuItem menuItem = menuDao.getMenuItemsByName(orderItem.getProductName());
                if (menuItem != null && menuItem.getStock() < orderItem.getQuantity()) {
                    showErrorAlert("Stock Error", "Not enough stock for " + orderItem.getProductName());
                    isStockSufficient = false;
                    break;
                }
            }

            if (!isStockSufficient) {
                return; // Stop the confirmation process if stock is insufficient
            }

            // Deduct the ordered quantity from the stock
            for (OrderItem orderItem : orderItems) {
                MenuItem menuItem = menuDao.getMenuItemsByName(orderItem.getProductName());
                if (menuItem != null) {
                    menuItem.setStock(menuItem.getStock() - orderItem.getQuantity());
                    menuDao.updateMenuItem(menuItem); // Assume this method properly updates the stock in the database
                }
            }

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/restaurantmanagementsystem/pos/view/receipt.fxml"));
                Parent root = loader.load();
                ReceiptController controller = loader.getController();

                // Calculate the order details
                double subtotal = orderItems.stream()
                        .mapToDouble(item -> item.getQuantity() * item.getPrice())
                        .sum();
                double serviceCharge = subtotal * 0.10;
                double tax = subtotal * 0.06;
                double total = subtotal + serviceCharge + tax;

                // Pass order details to the ReceiptViewController
                controller.setReceiptDetails(orderItems, subtotal, serviceCharge, tax, total);

                // Add current orderItems to allConfirmedOrders
                allConfirmedOrders.addAll(orderItems);

                // Clear current order details
                orderItems.clear();
                orderDetailsTable.refresh();
                totalText.setText("Total: RM0.0");

                // Show order confirmed
                Alert orderConfirmedAlert = new Alert(Alert.AlertType.INFORMATION, "Order has been confirmed!");
                orderConfirmedAlert.showAndWait();

                // Show receipt window
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Receipt");
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
                // Handle exception
            }
        } else {
            // If user selects cancel, do nothing and return to order screen
        }
    }

    // Receipt Button Action
    @FXML
    private void handleReceiptAction() {
        if (allConfirmedOrders.isEmpty()) {
            Alert noOrdersAlert = new Alert(Alert.AlertType.INFORMATION, "There are no past orders to display.");
            noOrdersAlert.setHeaderText("No Past Orders");
            noOrdersAlert.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/restaurantmanagementsystem/pos/view/receipt.fxml"));
            Parent root = loader.load();
            ReceiptController controller = loader.getController();

            // Calculate the totals from allConfirmedOrders
            double subtotal = allConfirmedOrders.stream()
                    .mapToDouble(item -> item.getQuantity() * item.getPrice())
                    .sum();
            double serviceCharge = subtotal * 0.10;
            double tax = subtotal * 0.06;
            double total = subtotal + serviceCharge + tax;

            // Pass allConfirmedOrders details to the ReceiptController
            controller.setReceiptDetails(allConfirmedOrders, subtotal, serviceCharge, tax, total);

            // Show receipt window
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Cumulative Receipt");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle exception
        }
    }

    // Calculate Total
    private void calculateTotal() {
        double total = orderItems.stream()
                .mapToDouble(item -> item.getQuantity() * item.getPrice())
                .sum();
        totalText.setText(String.format("Total: RM%.2f", total));
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
