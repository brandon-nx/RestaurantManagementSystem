package com.restaurantmanagementsystem.pos.db;

import com.restaurantmanagementsystem.pos.model.Order;

import java.sql.*;

public class OrderDaoImpl implements OrderDao {

    @Override
    public Order createOrder(Order order) {
//        String insertOrderSql = "INSERT INTO orders (user_id, total_amount, status) VALUES (?, ?, ?);";
//
//        try (Connection conn = DatabaseConnector.getConnection();
//             PreparedStatement insertStmt = conn.prepareStatement(insertOrderSql, Statement.RETURN_GENERATED_KEYS)) {
//
//            insertStmt.setInt(1, order.getUserId());
//            insertStmt.setBigDecimal(2, order.getTotalAmount());
//            insertStmt.setString(3, order.getStatus());
//            int affectedRows = insertStmt.executeUpdate();
//
//            if (affectedRows == 0) {
//                throw new SQLException("Creating order failed, no rows affected.");
//            }
//
//            try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
//                if (generatedKeys.next()) {
//                    int orderId = generatedKeys.getInt(1);
//                    String formattedOrderId = String.format("O-%03d", orderId);
//                    order.setOrderId(formattedOrderId); // Assuming Order's setOrderId() accepts a String
//                    // Now you can use order which has the formattedOrderId
//                } else {
//                    throw new SQLException("Creating order failed, no ID obtained.");
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//            // Proper exception handling should be implemented
//        }
//        return order;
        return order;
    }
}
