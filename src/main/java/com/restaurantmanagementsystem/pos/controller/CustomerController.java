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
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class CustomerController {
    @FXML
    private Text welcomeText;
    @FXML
    private Text totalText;
    @FXML
    private Button signOutButton;
    @FXML
    private Button removeButton;

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


    @FXML
    public void initialize() {
        welcomeText.setText("Welcome, Customer");
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        orderDetailsTable.setItems(orderItems);
        loadMenuItems();
    }

    public void setUsername(String username) {
        welcomeText.setText("Welcome, " + username);
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

    @FXML
    private void handleAddItemAction(ActionEvent event) {
        Button addButton = (Button) event.getSource();
        OrderItem item = (OrderItem) addButton.getUserData();

        // Find existing item in the order
        Optional<OrderItem> existingItem = orderItems.stream()
                .filter(orderItem -> orderItem.getProductName().equals(item.getProductName()))
                .findFirst();

        if (existingItem.isPresent()) {
            // If the item already exists, increase the quantity
            existingItem.get().setQuantity(existingItem.get().getQuantity() + 1);
        } else {
            // If not, add the new item with quantity 1
            orderItems.add(new OrderItem(item.getProductName(), 1, item.getPrice()));
        }
        orderDetailsTable.refresh();
        calculateTotal();
    }

    private void calculateTotal() {
        double total = orderItems.stream()
                .mapToDouble(item -> item.getQuantity() * item.getPrice())
                .sum();
        totalText.setText(String.format("Total: $%.2f", total));
    }


    private void loadMenuItems() {
        List<MenuItem> menuItems = menuDao.getMenuItemsFromDatabase();
        HBox currentRow = createRow(); // We'll create the createRow method to handle row creation

        int count = 0;
        for (MenuItem menuItem : menuItems) {
            if (count % 4 == 0 && count > 0) { // Every 4 items, start a new row
                currentRow = createRow();
            }

            VBox itemVBox = new VBox(10);
            itemVBox.setAlignment(Pos.CENTER);
            ImageView imageView = createImageView(menuItem.getImagePath());
            Text itemName = new Text(menuItem.getName() + " $" + menuItem.getPrice());
            Button addButton = new Button("Add");
            addButton.setOnAction(this::handleAddItemAction);
            addButton.setUserData(new OrderItem(menuItem.getName(), 1, menuItem.getPrice()));

            itemVBox.getChildren().addAll(imageView, itemName, addButton);
            currentRow.getChildren().add(itemVBox);
            count++;
        }
    }

    private HBox createRow() {
        HBox newRow = new HBox(20);
        newRow.setAlignment(Pos.TOP_LEFT);
        centerVBox.getChildren().add(newRow);
        return newRow;
    }


    private ImageView createImageView(String imagePath) {
        ImageView imageView = null;
        try {
            Image image;
            // Convert to a URL if it's not already a valid one
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

    @FXML
    private void handleRemoveItemAction() {
        OrderItem selectedItem = orderDetailsTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            // Remove the selected item from the list
            orderItems.remove(selectedItem);
            orderDetailsTable.refresh();
            calculateTotal(); // Recalculate the total after removal
        } else {
            // Show alert if no item is selected
            showAlert("No item selected", "Please select an item to remove.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
