package com.restaurantmanagementsystem.pos.controller;

import com.restaurantmanagementsystem.pos.model.OrderItem;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReceiptController {
    @FXML
    private Text restaurantName;
    @FXML
    private Text restaurantInfo;
    @FXML
    private GridPane itemsGrid;
    @FXML
    private Label subtotalPrice;
    @FXML
    private Label serviceChargePrice;
    @FXML
    private Label taxPrice;
    @FXML
    private Label totalPrice;
    @FXML
    private Label footerLabel;

    public void setReceiptDetails(List<OrderItem> orderItems, double subtotal, double serviceCharge, double tax, double total) {
        itemsGrid.getChildren().clear(); // Clear previous items if needed

        // Create header with restaurant information
        restaurantName.setText("Big Bite Restaurant");
        restaurantInfo.setText("123, Jalan 123, One Heights,\nJohor, Malaysia\nTEL : 123456789");

        // For each OrderItem, create a row in the GridPane
        for (int i = 0; i < orderItems.size(); i++) {
            OrderItem item = orderItems.get(i);
            Label nameLabel = new Label(item.getProductName());
            Label quantityLabel = new Label(String.format("x%d", item.getQuantity()));
            Label priceLabel = new Label(String.format("$%.2f", item.getPrice()));

            itemsGrid.add(quantityLabel, 0, i);
            itemsGrid.add(nameLabel, 1, i);
            itemsGrid.add(priceLabel, 2, i);

            priceLabel.setAlignment(Pos.TOP_RIGHT);
            priceLabel.setMaxWidth(Double.MAX_VALUE);
            GridPane.setHgrow(priceLabel, Priority.ALWAYS);
        }

        // Set text for subtotal, service charge, etc.
        subtotalPrice.setText(String.format("$%.2f", subtotal));
        serviceChargePrice.setText(String.format("$%.2f", serviceCharge));
        taxPrice.setText(String.format("$%.2f", tax));
        totalPrice.setText(String.format("$%.2f", total));

        // Set footer date and time
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        footerLabel.setText(String.format("Date: %s    Time: %s",
                LocalDate.now().format(dateFormatter),
                LocalTime.now().format(timeFormatter)));
    }
}
