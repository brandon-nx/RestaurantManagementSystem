package com.restaurantmanagementsystem.pos.controller;

import com.restaurantmanagementsystem.pos.model.Order;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

public class ViewOrdersController {
    @FXML
    private TableColumn<Order, String> orderIdColumn, customerNameColumn, totalAmountColumn, statusColumn, orderDateColumn;
    @FXML
    private TableColumn<Order, Void> actionColumn;
    @FXML
    private TableView<Order> ordersTableView;

    @FXML
    public void initialize() {
        // Initialize the action column with the button cell factory
        actionColumn.setCellFactory(getButtonCellFactory());
        // Initialize the TableView with data
        loadOrders();
    }

    private void loadOrders() {
        // Fetch the orders from the database and populate the TableView
        // Example: ordersTableView.setItems(FXCollections.observableList(orders));
    }

    private Callback<TableColumn<Order, Void>, TableCell<Order, Void>> getButtonCellFactory() {
        return new Callback<TableColumn<Order, Void>, TableCell<Order, Void>>() {
            @Override
            public TableCell<Order, Void> call(final TableColumn<Order, Void> param) {
                final TableCell<Order, Void> cell = new TableCell<Order, Void>() {
                    private final Button btn = new Button("View Details");
                    {
                        btn.setOnAction((ActionEvent event) -> {
                            Order data = getTableView().getItems().get(getIndex());
                            // Handle the button action
                            viewOrderDetails(data);
                        });
                    }
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : btn);
                    }
                };
                return cell;
            }
        };
    }

    private void viewOrderDetails(Order order) {
        // Implement the logic to view order details
        System.out.println("Viewing order: " + order);
    }
}
