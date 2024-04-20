package com.restaurantmanagementsystem.pos.controller;

import com.restaurantmanagementsystem.pos.db.OrderDao;
import com.restaurantmanagementsystem.pos.db.OrderDaoImpl;
import com.restaurantmanagementsystem.pos.model.Order;
import com.restaurantmanagementsystem.pos.model.OrderItem;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OrdersController {
    @FXML
    private TableView<Order> ordersTableView;
    @FXML
    private TableColumn<Order, String> orderIdColumn, orderItemsColumn, statusColumn, customerNameColumn;
    @FXML
    private TableColumn<Order, Double> totalAmountColumn;
    @FXML
    private TableColumn<Order, Void> actionColumn;
    private OrderDao orderDao = new OrderDaoImpl();

    @FXML
    public void initialize() {
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        orderItemsColumn.setCellValueFactory(new PropertyValueFactory<>("orderItemsAsString"));
        totalAmountColumn.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));

        setUpActionColumn();

        loadOrders();
    }

    private void setUpActionColumn() {
        actionColumn.setCellFactory(col -> new TableCell<Order, Void>() {
            private final Button doneButton = new Button("Done");
            private final Button cancelButton = new Button("Cancel");
            private final Button pendingButton = new Button("Pending");
            private final HBox buttonGroup = new HBox(5, doneButton, cancelButton, pendingButton);

            {
                doneButton.setOnAction(event -> doneOrder(getTableView().getItems().get(getIndex())));
                cancelButton.setOnAction(event -> cancelOrder(getTableView().getItems().get(getIndex())));
                pendingButton.setOnAction(event -> pendingOrder(getTableView().getItems().get(getIndex())));
            }

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

    private void loadOrders() {
        List<Order> orders = orderDao.getAllOrders();
        ordersTableView.setItems(FXCollections.observableArrayList(orders));
    }

    private void doneOrder(Order order) {
        updateOrderStatus(order, "done");
    }

    private void cancelOrder(Order order) {
        updateOrderStatus(order, "cancelled");
    }
    private void pendingOrder(Order order) {
        updateOrderStatus(order, "pending");
    }

    private void updateOrderStatus(Order order, String newStatus) {
        order.setStatus(newStatus);
        orderDao.updateOrderStatus(order);
        loadOrders();
    }
}
