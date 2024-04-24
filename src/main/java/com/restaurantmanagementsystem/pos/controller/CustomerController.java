package com.restaurantmanagementsystem.pos.controller;

import com.restaurantmanagementsystem.pos.db.MenuDao;
import com.restaurantmanagementsystem.pos.db.MenuDaoImpl;
import com.restaurantmanagementsystem.pos.db.OrderDao;
import com.restaurantmanagementsystem.pos.db.OrderDaoImpl;
import com.restaurantmanagementsystem.pos.model.*;

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
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CustomerController {
    @FXML
    private Button clearOrderButton, confirmOrderButton, receiptButton, appetizersButton, entreesButton, sidesButton, dessertsButton, beveragesButton, signOutButton;
    @FXML
    private Text welcomeText, totalText;
    @FXML
    private TableView<OrderItem> orderDetailsTable;
    @FXML
    private TableColumn<OrderItem, String> productNameColumn;
    @FXML
    private TableColumn<OrderItem, Number> quantityColumn, priceColumn;
    @FXML
    private VBox centerVBox;
    private User loggedInUser;
    private final MenuDao menuDao = new MenuDaoImpl();
    private final OrderDao orderDao = new OrderDaoImpl();
    private ObservableList<OrderItem> orderItems = FXCollections.observableArrayList();
    private List<OrderItem> allConfirmedOrders = new ArrayList<>();

    @FXML
    public void initialize() {
        setUpTable();
        loadCategory("Appetizers");
    }

    private void setUpTable() {
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        orderDetailsTable.setItems(orderItems);
        handleRemoveItemAction();
    }

    public void setUser(User user) {
        this.loggedInUser = user;
        welcomeText.setText(String.format("Welcome, %s", user.getUsername()));
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
        List<MenuItem> allItems = menuDao.getMenuItemsByCategory(category);
        List<MenuItem> filteredItems = allItems.stream()
                .filter(item -> {
                    return !"unavailable".equals(item.getStatus());
                })
                .collect(Collectors.toList());
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
        Text itemName = new Text(menuItem.getName());
        itemName.setFont(Font.font("System", FontWeight.BOLD, 12));

        Text itemPrice = new Text(String.format("RM %.2f", menuItem.getPrice()));
        Button addButton = new Button("Add");
        addButton.setId("addButton");
        addButton.setOnAction(this::handleAddItemAction);
        addButton.setUserData(new OrderItem(menuItem.getProductId(), menuItem.getName(), 1, menuItem.getPrice()));

        if (menuItem.getStock() <= 0) {
            addButton.setDisable(true);
            addButton.setTooltip(new Tooltip("Out of stock"));
        }

        itemVBox.getChildren().addAll(imageView, itemName, itemPrice, addButton);
        return itemVBox;
    }

    private ImageView createImageView(String imagePath) {
        ImageView imageView;
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
            imageView.setFitHeight(115);
            imageView.setFitWidth(115);
            imageView.setSmooth(true);

        } catch (Exception e) {
            e.printStackTrace();
            imageView = new ImageView(new Image("logo.png"));
            imageView.setFitHeight(115);
            imageView.setFitWidth(115);
            imageView.setSmooth(true);
        }
        return imageView;
    }

    // Sign Out Button
    @FXML
    private void handleSignOutAction() {
        if (AlertUtils.showConfirmationAlert("Sign Out", "Are you sure you want to sign out?")) {
           try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/restaurantmanagementsystem/pos/view/login.fxml")); // Correct the path if necessary.
                Parent loginView = loader.load();

                Stage stage = (Stage) signOutButton.getScene().getWindow();
                stage.setScene(new Scene(loginView));
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Add Item Button
    @FXML
    private void handleAddItemAction(ActionEvent event) {
        Button addButton = (Button) event.getSource();
        MenuItem menuItem = menuDao.getMenuItemsByName(((OrderItem) addButton.getUserData()).getProductName());

        if (menuItem != null) {
            if (menuItem.getStock() > 0) {
                OrderItem orderItem = new OrderItem(menuItem.getProductId(), menuItem.getName(), 1, menuItem.getPrice());
                addOrUpdateOrderItem(orderItem, menuItem);
            } else {
                AlertUtils.showErrorAlert("Out of Stock", "The selected item is out of stock.");
            }
        } else {
            AlertUtils.showErrorAlert("Item Not Found", "Could not find the items in the menu.");
        }
    }

    // Helper method to add or update an OrderItem in the order
    private void addOrUpdateOrderItem(OrderItem itemToAdd, MenuItem menuItem) {
        Optional<OrderItem> existingOrderItem = orderItems.stream()
                .filter(orderItem -> orderItem.getProductName().equals(itemToAdd.getProductName()))
                .findFirst();

        if (existingOrderItem.isPresent()) {
            OrderItem existingItem = existingOrderItem.get();
            if (existingItem.getQuantity() + 1 <= menuItem.getStock()) {
                existingItem.setQuantity(existingItem.getQuantity() + 1);
            } else {
                AlertUtils.showErrorAlert("Stock Error", "Not enough stock for " + itemToAdd.getProductName());
                return;
            }
        } else {
            if (menuItem.getStock() > 0) {
                orderItems.add(new OrderItem(menuItem.getProductId(), menuItem.getName(), 1, menuItem.getPrice()));
            } else {
                AlertUtils.showErrorAlert("Stock Error", "Not enough stock for " + itemToAdd.getProductName());
                return;
            }
        }

        orderDetailsTable.refresh(); // Refresh the TableView to show updated quantities
        totalText.setText(String.format("Total: RM%.2f", calculateSubtotal()));
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
                    totalText.setText(String.format("Total: RM%.2f", calculateSubtotal()));
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
        if (!orderItems.isEmpty() && AlertUtils.showConfirmationAlert("Clear Order", "Clear the entire order?")) {
            orderItems.clear();
            orderDetailsTable.refresh();
            totalText.setText(String.format("Total: RM%.2f", calculateSubtotal()));
        } else if (orderItems.isEmpty()) {
            AlertUtils.showErrorAlert("Empty Order", "No items in the order.");
        }
    }

    // Confirm Order Button
    @FXML
    private void handleConfirmOrderAction() {
        if (orderItems.isEmpty()) {
            AlertUtils.showConfirmationAlert("Empty Order", "No items in the order.");
            return;
        }

        if (!AlertUtils.showConfirmationAlert("Confirm Order", "Do you want to confirm the order?")) {
            return;
        }

        if (!checkStockAvailability()) {
            return;
        }

        updateStock();
        createOrder();
        showReceiptWindow();

        orderItems.clear();
        orderDetailsTable.refresh();
        totalText.setText(String.format("Total: RM%.2f", calculateSubtotal()));
    }

    private boolean checkStockAvailability() {
        for (OrderItem orderItem : orderItems) {
            MenuItem menuItem = menuDao.getMenuItemsByName(orderItem.getProductName());
            if (menuItem != null && menuItem.getStock() < orderItem.getQuantity()) {
                AlertUtils.showErrorAlert("Stock Error", "Not enough stock for " + orderItem.getProductName());
                return false;
            }
        }
        return true;
    }

    private void updateStock() {
        for (OrderItem orderItem : orderItems) {
            MenuItem menuItem = menuDao.getMenuItemsByName(orderItem.getProductName());
            if (menuItem != null) {
                menuItem.setStock(menuItem.getStock() - orderItem.getQuantity());
                menuDao.updateMenuItem(menuItem);
            }
        }
    }

    private void createOrder() {
        double subtotal = calculateSubtotal();
        double serviceCharge = calculateServiceCharge(subtotal);
        double tax = calculateTax(subtotal);
        double total = calculateTotal(subtotal, serviceCharge, tax);

        Order newOrder = new Order();
        newOrder.setUserId(loggedInUser.getUserId());
        newOrder.setOrderItems(new ArrayList<>(orderItems));
        newOrder.setTotalAmount(total);
        String orderId = orderDao.addOrder(newOrder);

        newOrder.setOrderId(orderId);
        orderDao.addOrderItems(new ArrayList<>(orderItems), orderId);

        // Add current orderItems to allConfirmedOrders
        allConfirmedOrders.addAll(orderItems);

        // Show order confirmed
        AlertUtils.showInformationAlert("Order Confirmed", "Order has been confirmed!");
    }

    private double calculateSubtotal() {
        return orderItems.stream()
                .mapToDouble(item -> item.getQuantity() * item.getPrice())
                .sum();
    }

    private double calculateServiceCharge(double subtotal) {
        return subtotal * 0.10;
    }

    private double calculateTax(double subtotal) {
        return subtotal * 0.06;
    }

    private double calculateTotal(double subtotal, double serviceCharge, double tax) {
        return subtotal + serviceCharge + tax;
    }

    private void showReceiptWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/restaurantmanagementsystem/pos/view/receipt.fxml"));
            Parent root = loader.load();
            ReceiptController controller = loader.getController();

            // Pass order details to the ReceiptViewController
            controller.setReceiptDetails(orderItems,
                    calculateSubtotal(),
                    calculateServiceCharge(calculateSubtotal()),
                    calculateTax(calculateSubtotal()),
                    calculateTotal(calculateSubtotal(), calculateServiceCharge(calculateSubtotal()), calculateTax(calculateSubtotal())));

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
}