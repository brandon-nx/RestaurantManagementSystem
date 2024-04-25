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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CustomerController {
    @FXML
    public BorderPane mainBorderPane;
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

    ObservableList<OrderItem> orderItems = FXCollections.observableArrayList();
    private List<OrderItem> allConfirmedOrders = new ArrayList<>();

    private static final int MAX_ITEMS_PER_ROW = 4;
    private static final int IMAGE_SIZE = 115;


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




    // Sidebar menu with all food categories to choose from
    @FXML
    private void handleCategoryAction(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        String category = getButtonCategory(clickedButton);
        loadCategory(category);
    }

    private String getButtonCategory(Button button) {
        String buttonId = button.getId();
        switch (buttonId) {
            case "appetizersButton":
                return "Appetizers";
            case "entreesButton":
                return "Entrées";
            case "sidesButton":
                return "Sides";
            case "dessertsButton":
                return "Desserts";
            case "beveragesButton":
                return "Beverages";
            default:
                return "";
        }
    }

    private void loadCategory(String category) {
        List<MenuItem> menuItems = menuDao.getMenuItemsByCategory(category);
        displayMenuItems(menuItems);
    }

    // Sign out action under all the categories
    @FXML
    private void handleSignOutAction() {
        if (AlertUtils.showConfirmationAlert("Sign Out", "Are you sure you want to sign out?")) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/restaurantmanagementsystem/pos/view/login.fxml"));
                Parent loginView = loader.load();
                Stage stage = (Stage) mainBorderPane.getScene().getWindow();
                stage.setScene(new Scene(loginView));
                stage.show();
            } catch (IOException e) {
                System.err.println("Failed to Sign Out: " + e.getMessage());
                AlertUtils.showErrorAlert("Error", "Failed to sign out. Please try again.");
            }
        }
    }





    // Create and display all available menu items
    private void displayMenuItems(List<MenuItem> menuItems) {
        centerVBox.getChildren().clear();
        HBox currentRow = new HBox(20);
        centerVBox.getChildren().add(currentRow);

        int itemsInRow  = 0;
        for (MenuItem menuItem : menuItems) {
            if (itemsInRow  == MAX_ITEMS_PER_ROW) {
                currentRow = new HBox(20);
                centerVBox.getChildren().add(currentRow);
                itemsInRow  = 0;
            }

            VBox itemVBox = createMenuItemVBox(menuItem);
            currentRow.getChildren().add(itemVBox);

            if (menuItem.getStock() <= 0) {
                disableAddButton(itemVBox);
            }

            itemsInRow++;
        }
    }

    private VBox createMenuItemVBox(MenuItem menuItem) {
        VBox itemVBox = new VBox(10);
        itemVBox.setAlignment(Pos.CENTER);
        ImageView imageView = createImageView(menuItem.getImagePath());
        Text itemName = createItemNameText(menuItem.getName());
        Text itemPrice = createItemPriceText(menuItem.getPrice());
        Button addButton = createAddButton(menuItem);

        itemVBox.getChildren().addAll(imageView, itemName, itemPrice, addButton);
        return itemVBox;
    }

    private ImageView createImageView(String imagePath) {
        try {
            Image image = new Image(imagePath);
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(IMAGE_SIZE);
            imageView.setFitWidth(IMAGE_SIZE);
            imageView.setSmooth(true);
            return imageView;
        } catch (Exception e) {
            System.err.println("Error loading image: " + e.getMessage());
            AlertUtils.showErrorAlert("Image Load Error", "Failed to load image.");
            return createDefaultImageView();
        }
    }

    private ImageView createDefaultImageView() {
        Image defaultImage = new Image("logo.png");
        ImageView imageView = new ImageView(defaultImage);
        imageView.setFitHeight(IMAGE_SIZE);
        imageView.setFitWidth(IMAGE_SIZE);
        imageView.setSmooth(true);
        return imageView;
    }

    private Text createItemNameText(String name) {
        Text itemName = new Text(name);
        itemName.setFont(Font.font("System", FontWeight.BOLD, 12));
        return itemName;
    }

    private Text createItemPriceText(double price) {
        return new Text(String.format("RM %.2f", price));
    }

    private Button createAddButton(MenuItem menuItem) {
        Button addButton = new Button("Add");
        addButton.setId("addButton");
        addButton.setOnAction(this::handleAddItemAction);
        addButton.setUserData(new OrderItem(menuItem.getProductId(), menuItem.getName(), 1, menuItem.getPrice()));
        return addButton;
    }

    private void disableAddButton(VBox itemVBox) {
        Button addButton = (Button) itemVBox.lookup("#addButton");
        if (addButton != null) {
            addButton.setDisable(true);
            addButton.setTooltip(new Tooltip("Out of stock"));
        }
    }





    // Add Item Button
    @FXML
    private void handleAddItemAction(ActionEvent event) {
        Button addButton = (Button) event.getSource();
        OrderItem itemToAdd = extractOrderItemFromButton(addButton);
        if (itemToAdd != null) {
            MenuItem menuItem = findMenuItemByName(itemToAdd.getProductName());
            if (menuItem != null) {
                if (menuItem.getStock() > 0) {
                    addOrUpdateOrderItem(itemToAdd, menuItem);
                } else {
                    AlertUtils.showErrorAlert("Out of Stock", "The selected item is out of stock.");
                }
            } else {
                AlertUtils.showErrorAlert("Item Not Found", "Could not find the item in the menu.");
            }
        }
    }

    private OrderItem extractOrderItemFromButton(Button addButton) {
        Object userData = addButton.getUserData();
        if (userData instanceof OrderItem) {
            return (OrderItem) userData;
        } else {
            return null;
        }
    }

    private MenuItem findMenuItemByName(String productName) {
        return menuDao.getMenuItemsByName(productName);
    }

    private void addOrUpdateOrderItem(OrderItem itemToAdd, MenuItem menuItem) {
        Optional<OrderItem> existingOrderItem = findExistingOrderItem(itemToAdd);
        if (existingOrderItem.isPresent()) {
            updateExistingOrderItem(existingOrderItem.get(), menuItem);
        } else {
            createNewOrderItem(itemToAdd, menuItem);
        }
        updateOrderDetails();
    }

    private Optional<OrderItem> findExistingOrderItem(OrderItem itemToAdd) {
        for (OrderItem orderItem : orderItems) {
            if (orderItem.getProductName().equals(itemToAdd.getProductName())) {
                return Optional.of(orderItem);
            }
        }
        return Optional.empty();
    }

    private void updateExistingOrderItem(OrderItem existingItem, MenuItem menuItem) {
        if (existingItem.getQuantity() + 1 <= menuItem.getStock()) {
            existingItem.setQuantity(existingItem.getQuantity() + 1);
        } else {
            AlertUtils.showErrorAlert("Stock Error", "Not enough stock for " + existingItem.getProductName());
        }
    }

    private void createNewOrderItem(OrderItem itemToAdd, MenuItem menuItem) {
        if (menuItem.getStock() > 0) {
            orderItems.add(new OrderItem(menuItem.getProductId(), menuItem.getName(), 1, menuItem.getPrice()));
        } else {
            AlertUtils.showErrorAlert("Stock Error", "Not enough stock for " + itemToAdd.getProductName());
        }
    }

    private void updateOrderDetails() {
        orderDetailsTable.refresh();
        totalText.setText(String.format("Total: RM%.2f", calculateSubtotal()));
    }





    // Remove Button
    private void handleRemoveItemAction() {
        TableColumn<OrderItem, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(createButtonCellFactory());
        orderDetailsTable.getColumns().add(actionCol);
    }

    private Callback<TableColumn<OrderItem, Void>, TableCell<OrderItem, Void>> createButtonCellFactory() {
        return param -> new TableCell<>() {
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
        if (!validateOrder()) {
            return;
        }

        processOrder();
        refreshUI();
    }

    private boolean validateOrder() {
        if (orderItems.isEmpty()) {
            AlertUtils.showConfirmationAlert("Empty Order", "No items in the order.");
            return false;
        }

        if (!AlertUtils.showConfirmationAlert("Confirm Order", "Do you want to confirm the order?")) {
            return false;
        }

        return checkStockAvailability();
    }

    private void processOrder() {
        updateStock();
        createOrder();
        showReceiptWindow();
    }

    private void refreshUI() {
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

        AlertUtils.showInformationAlert("Order Confirmed", "Order has been confirmed!");
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
            System.err.println("Failed to load the receipt view: " + e.getMessage());
            AlertUtils.showErrorAlert("Loading Error", "Failed to load the receipt view.");
        }
    }






    // Receipt Button Action
    @FXML
    private void handleReceiptAction() {
        if (allConfirmedOrders.isEmpty()) {
            AlertUtils.showInformationAlert("No Past Orders", "There are no past orders to display.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/restaurantmanagementsystem/pos/view/receipt.fxml"));
            Parent root = loader.load();
            ReceiptController controller = loader.getController();

            // Calculate the totals from allConfirmedOrders
            double subtotal = 0.0;
            for (OrderItem orderItem : allConfirmedOrders) {
                subtotal += orderItem.getQuantity() * orderItem.getPrice();
            }
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
            System.err.println("Failed to load the receipt view: " + e.getMessage());
            AlertUtils.showErrorAlert("Loading Error", "Failed to load the receipt view.");
        }
    }


    double calculateSubtotal() {
        double subtotal = 0.0;

        for (OrderItem item : orderItems) {
            double itemTotal = item.getQuantity() * item.getPrice();
            subtotal += itemTotal;
        }

        return subtotal;
    }

    double calculateServiceCharge(double subtotal) {
        return subtotal * 0.10;
    }

    double calculateTax(double subtotal) {
        return subtotal * 0.06;
    }

    double calculateTotal(double subtotal, double serviceCharge, double tax) {
        return subtotal + serviceCharge + tax;
    }
}