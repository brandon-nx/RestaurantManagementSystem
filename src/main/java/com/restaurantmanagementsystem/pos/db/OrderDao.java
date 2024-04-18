package com.restaurantmanagementsystem.pos.db;

import com.restaurantmanagementsystem.pos.model.Order;
import com.restaurantmanagementsystem.pos.model.OrderItem;

import java.util.List;

public interface OrderDao {
    String addOrder(Order order);
    void addOrderItems(List<OrderItem> orderItems, String orderId);

    List<Order> getAllOrders();
    void updateOrderStatus(Order order);
    String generateNewOrderId();
}
