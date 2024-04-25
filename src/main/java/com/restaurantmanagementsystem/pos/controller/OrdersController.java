package com.restaurantmanagementsystem.pos.controller;

import com.restaurantmanagementsystem.pos.db.OrderDao;
import com.restaurantmanagementsystem.pos.db.OrderDaoImpl;
import com.restaurantmanagementsystem.pos.model.AlertUtils;
import com.restaurantmanagementsystem.pos.model.Order;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.util.List;

public class OrdersController {
    @FXML
    TableView<Order> ordersTableView;
    @FXML
    TableColumn<Order, String> orderIdColumn;
    @FXML
    TableColumn<Order, String> orderItemsColumn;
    @FXML
    TableColumn<Order, String> statusColumn;
    @FXML
    TableColumn<Order, String> customerNameColumn;
    @FXML
    TableColumn<Order, Double> totalAmountColumn;
    @FXML
    TableColumn<Order, Void> actionColumn;
    OrderDao orderDao = new OrderDaoImpl();

    @FXML
    public void initialize() {
        setupColumns();
        setUpActionColumn();
        styleRowsBasedOnStatus();
        loadOrders();
    }

    private void setupColumns() {
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        orderItemsColumn.setCellValueFactory(new PropertyValueFactory<>("orderItemsAsString"));
        totalAmountColumn.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
    }

    // Configures action buttons within each row
    private void setUpActionColumn() {
        actionColumn.setCellFactory(col -> new TableCell<Order, Void>() {
            private final Button doneButton = createActionButton("Done", this, "done");
            private final Button cancelButton = createActionButton("Cancel", this, "cancelled");
            private final Button pendingButton = createActionButton("Pending", this, "pending");
            private final HBox buttonGroup = new HBox(5, doneButton, cancelButton, pendingButton);

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttonGroup);
                }
            }
        });
    }

    private Button createActionButton(String buttonText, TableCell<Order, Void> cell, String status) {
        Button button = new Button(buttonText);
        button.setOnAction(event -> handleOrderAction(cell, status));
        return button;
    }

    public void handleOrderAction(TableCell<Order, Void> cell, String status) {
        TableView<Order> table = cell.getTableView();
        if (table != null) {
            int index = cell.getIndex();
            Order order = table.getItems().get(index);
            if (order != null) {
                if (AlertUtils.showConfirmationAlert("Confirm Status Change", "Are you sure you want to change the order status to " + status + " ?")) {
                    updateOrderStatus(order, status);
                }
            }
        }
    }

    void loadOrders() {
        List<Order> orders = orderDao.getAllOrders();
        ordersTableView.setItems(FXCollections.observableArrayList(orders));
    }

    void updateOrderStatus(Order order, String newStatus) {
        order.setStatus(newStatus);
        orderDao.updateOrderStatus(order);
        loadOrders();
    }

    // Styles the rows based on the status of the order
    private void styleRowsBasedOnStatus() {
        ordersTableView.setRowFactory(tv -> new TableRow<Order>() {
            @Override
            protected void updateItem(Order item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else {
                    switch (item.getStatus()) {
                        case "done":
                            setStyle("-fx-background-color: lightgreen;");
                            break;
                        case "cancelled":
                            setStyle("-fx-background-color: salmon;");
                            break;
                        case "pending":
                            setStyle("-fx-background-color: lightyellow;");
                            break;
                        default:
                            setStyle("");
                            break;
                    }
                }
            }
        });
    }
}