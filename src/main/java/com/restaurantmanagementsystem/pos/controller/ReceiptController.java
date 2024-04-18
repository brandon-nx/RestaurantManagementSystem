package com.restaurantmanagementsystem.pos.controller;

import com.restaurantmanagementsystem.pos.model.OrderItem;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.text.NumberFormat;

public class ReceiptController {
    @FXML
    private Text restaurantName, restaurantInfo;
    @FXML
    private GridPane itemsGrid;
    @FXML
    private Label subtotalPrice, serviceChargePrice, taxPrice, totalPrice, footerLabel;

    public void setReceiptDetails(List<OrderItem> orderItems, double subtotal, double serviceCharge, double tax, double total) {
        itemsGrid.getChildren().clear(); // Clear previous items if needed

        // Setup the NumberFormat for currency based on Locale
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "MY"));

        // Display restaurant information
        restaurantName.setText("Big Bite Restaurant");
        restaurantInfo.setText("123, Jalan 123, One Heights,\nJohor, Malaysia\nTEL : 123456789");

        if (orderItems.isEmpty()) {
            Label noItemsLabel = new Label("No items in this order.");
            itemsGrid.add(noItemsLabel, 0, 0, 3, 1);
            GridPane.setHalignment(noItemsLabel, HPos.CENTER);
        } else {
            // For each OrderItem, create a row in the GridPane
            for (int i = 0; i < orderItems.size(); i++) {
                OrderItem item = orderItems.get(i);
                Label nameLabel = new Label(item.getProductName());
                Label quantityLabel = new Label(String.format("x%d", item.getQuantity()));
                Label priceLabel = new Label(currencyFormat.format(item.getPrice()));

                nameLabel.setMaxWidth(Double.MAX_VALUE);
                GridPane.setHalignment(priceLabel, HPos.RIGHT);
                GridPane.setHgrow(nameLabel, Priority.ALWAYS);

                itemsGrid.add(quantityLabel, 0, i);
                itemsGrid.add(nameLabel, 1, i);
                itemsGrid.add(priceLabel, 2, i);
            }
        }

        // Set text for subtotal, service charge, etc.
        subtotalPrice.setText(currencyFormat.format(subtotal));
        serviceChargePrice.setText(currencyFormat.format(serviceCharge));
        taxPrice.setText(currencyFormat.format(tax));
        totalPrice.setText(currencyFormat.format(total));

        // Set footer date and time
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        footerLabel.setText(String.format("Date: %s    Time: %s",
                LocalDate.now().format(dateFormatter),
                LocalTime.now().format(timeFormatter)));
    }
}
