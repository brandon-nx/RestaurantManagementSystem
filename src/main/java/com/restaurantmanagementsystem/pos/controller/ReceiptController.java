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
    private static final Locale MY_LOCALE = new Locale("en", "MY");
    private static final String RESTAURANT_NAME = "Big Bite Restaurant";
    private static final String RESTAURANT_INFO = "123, Jalan 123, One Heights,\nJohor, Malaysia\nTEL : 123456789";
    private static final String DATE_PATTERN = "dd/MM/yyyy";
    private static final String TIME_PATTERN = "HH:mm:ss";
    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(MY_LOCALE);

    @FXML
    private Text restaurantName, restaurantInfo;
    @FXML
    private GridPane itemsGrid;
    @FXML
    private Label subtotalPrice, serviceChargePrice, taxPrice, totalPrice, footerLabel;

    public void setReceiptDetails(List<OrderItem> orderItems, double subtotal, double serviceCharge, double tax, double total) {
        clearItemsGrid();
        displayRestaurantInfo();
        addOrderItemsToGrid(orderItems);
        displayPrices(subtotal, serviceCharge, tax, total);
        setFooterDateAndTime();
    }

    private void clearItemsGrid() {
        itemsGrid.getChildren().clear();
    }

    private void displayRestaurantInfo() {
        restaurantName.setText(RESTAURANT_NAME);
        restaurantInfo.setText(RESTAURANT_INFO);
    }

    private void addOrderItemsToGrid(List<OrderItem> orderItems) {
        if (orderItems.isEmpty()) {
            itemsGrid.add(createCenterAlignedLabel("No items in this order."), 0, 0, 3, 1);
        } else {
            for (int i = 0; i < orderItems.size(); i++) {
                OrderItem item = orderItems.get(i);
                itemsGrid.add(new Label(String.format("x%d", item.getQuantity())), 0, i);
                Label nameLabel = createNameLabel(item.getProductName());
                Label priceLabel = createPriceLabel(item.getPrice());
                itemsGrid.addRow(i, nameLabel, priceLabel);
            }
        }
    }

    private Label createCenterAlignedLabel(String text) {
        Label label = new Label(text);
        GridPane.setHalignment(label, HPos.CENTER);
        return label;
    }

    private Label createNameLabel(String name) {
        Label nameLabel = new Label(name);
        nameLabel.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(nameLabel, Priority.ALWAYS);
        return nameLabel;
    }

    private Label createPriceLabel(double price) {
        Label priceLabel = new Label(CURRENCY_FORMAT.format(price));
        GridPane.setHalignment(priceLabel, HPos.RIGHT);
        return priceLabel;
    }

    private void displayPrices(double subtotal, double serviceCharge, double tax, double total) {
        subtotalPrice.setText(CURRENCY_FORMAT.format(subtotal));
        serviceChargePrice.setText(CURRENCY_FORMAT.format(serviceCharge));
        taxPrice.setText(CURRENCY_FORMAT.format(tax));
        totalPrice.setText(CURRENCY_FORMAT.format(total));
    }

    private void setFooterDateAndTime() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(TIME_PATTERN);
        footerLabel.setText(String.format("Date: %s    Time: %s",
                LocalDate.now().format(dateFormatter),
                LocalTime.now().format(timeFormatter)));
    }
}
