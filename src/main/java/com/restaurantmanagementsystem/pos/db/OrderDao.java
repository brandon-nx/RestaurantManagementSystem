package com.restaurantmanagementsystem.pos.db;

import com.restaurantmanagementsystem.pos.model.Order;

public interface OrderDao {
    Order createOrder(Order order);
}
