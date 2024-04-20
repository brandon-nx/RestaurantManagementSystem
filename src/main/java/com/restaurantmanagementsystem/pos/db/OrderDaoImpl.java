package com.restaurantmanagementsystem.pos.db;

import com.restaurantmanagementsystem.pos.model.Order;
import com.restaurantmanagementsystem.pos.model.OrderItem;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

public class OrderDaoImpl implements OrderDao {
    @Override
    public String addOrder(Order order) {
        String newOrderId = generateNewOrderId();
        String sql = "INSERT INTO orders (order_id, user_id, total_amount, status) VALUES (?, ?, ?, 'pending')";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newOrderId);
            pstmt.setString(2, order.getUserId());
            pstmt.setDouble(3, order.getTotalAmount());
            pstmt.executeUpdate();
            return newOrderId; // Return the new ID
        } catch (SQLException e) {
            e.printStackTrace();
            // Proper error handling should be done here
            return null; // Return null in case of error
        }
    }


    @Override
    public void addOrderItems(List<OrderItem> orderItems, String orderId) {
        String sql = "INSERT INTO order_items (order_item_id, order_id, product_id, quantity, price, product_name) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (OrderItem item : orderItems) {
                String newOrderItemId = generateNewOrderItemId();
                pstmt.setString(1, newOrderItemId);
                pstmt.setString(2, orderId);
                pstmt.setString(3, item.getProductId());
                pstmt.setInt(4, item.getQuantity());
                pstmt.setDouble(5, item.getPrice());
                pstmt.setString(6, item.getProductName());
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Proper error handling should be done here
        }
    }

    @Override
    public void updateOrderStatus(Order order) {
        String sql = "UPDATE orders SET status = ?, updated_at = NOW() WHERE order_id = ?"; // Adjust SQL to match your schema

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, order.getStatus());
            pstmt.setString(2, order.getOrderId());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace(); // Replace with proper error handling
        }
    }

    @Override
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.order_id, o.user_id, u.username AS customer_name, o.total_amount, o.status, o.created_at, o.updated_at " +
                "FROM orders o " +
                "JOIN users u ON o.user_id = u.user_id";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Order order = new Order();
                order.setOrderId(rs.getString("order_id"));
                order.setUserId(rs.getString("user_id"));
                order.setCustomerName(rs.getString("customer_name")); // This assumes you have a customerName field in Order
                order.setTotalAmount(rs.getDouble("total_amount"));
                order.setStatus(rs.getString("status"));
                order.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                order.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                order.setOrderItems(getOrderItemsByOrderId(order.getOrderId()));
                orders.add(order);
            }


        } catch (SQLException e) {
            e.printStackTrace(); // Consider more robust error handling
        }
        return orders;
    }


    private List<OrderItem> getOrderItemsByOrderId(String orderId) {
        List<OrderItem> orderItems = new ArrayList<>();
        String sql = "SELECT * FROM order_items WHERE order_id = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, orderId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    OrderItem item = new OrderItem();
                    item.setOrderId(orderId);
                    item.setProductName(rs.getString("product_name")); // replace with your column name
                    item.setPrice(rs.getDouble("price")); // replace with your column name
                    item.setQuantity(rs.getInt("quantity")); // replace with your column name
                    orderItems.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return orderItems;
    }

    @Override
    public List<Order> getOrdersByDate(LocalDate date) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE DATE(created_at) = ?"; // Assuming your database supports this syntax

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, Date.valueOf(date));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Order order = new Order();
                    order.setOrderId(rs.getString("order_id"));
                    order.setUserId(rs.getString("user_id"));
                    order.setTotalAmount(rs.getDouble("total_amount"));
                    order.setStatus(rs.getString("status"));
                    orders.add(order);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orders;
    }

    @Override
    public double getTotalIncome() {
        String sql = "SELECT SUM(total_amount) as totalIncome FROM orders WHERE status = 'done'";
        double totalIncome = 0.0;

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                totalIncome = rs.getDouble("totalIncome");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exception appropriately
        }

        return totalIncome;
    }

    @Override
    public int getTotalItemsSold() {
        String sql = "SELECT SUM(quantity) as totalItemsSold FROM order_items oi " +
                "JOIN orders o ON oi.order_id = o.order_id " +
                "WHERE o.status = 'done'";
        int totalItemsSold = 0;

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                totalItemsSold = rs.getInt("totalItemsSold");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exception appropriately
        }

        return totalItemsSold;
    }

    @Override
    public String getBestSeller() {
        String sql = "SELECT product_name, SUM(quantity) as totalQuantity " +
                "FROM order_items " +
                "GROUP BY product_name " +
                "ORDER BY totalQuantity DESC " +
                "LIMIT 1";
        String bestSeller = "Not available";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                bestSeller = rs.getString("product_name");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exception appropriately
        }

        return bestSeller;
    }

    @Override
    public Map<LocalDate, Double> getDailySales() {
        Map<LocalDate, Double> dailySales = new TreeMap<>();
        String sql = "SELECT DATE(created_at) as date, SUM(total_amount) as daily_total " +
                "FROM orders WHERE status = 'done' " +
                "GROUP BY date " +
                "ORDER BY date ASC";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                LocalDate date = rs.getDate("date").toLocalDate();
                Double dailyTotal = rs.getDouble("daily_total");
                dailySales.put(date, dailyTotal);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exception appropriately
        }

        return dailySales;
    }

    @Override
    public Map<YearMonth, Double> getMonthlySales() {
        String sql = "SELECT YEAR(created_at) AS year, MONTH(created_at) AS month, SUM(total_amount) AS total_sales " +
                "FROM orders " +
                "WHERE status = 'done' " +
                "GROUP BY YEAR(created_at), MONTH(created_at)";
        Map<YearMonth, Double> monthlySales = new HashMap<>();

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int year = rs.getInt("year");
                int month = rs.getInt("month");
                double totalSales = rs.getDouble("total_sales");
                YearMonth yearMonth = YearMonth.of(year, month);

                monthlySales.put(yearMonth, totalSales);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Consider more robust error handling, such as logging the error or throwing a custom exception
        }

        return monthlySales;
    }

    @Override
    public Map<Integer, Double> getAnnualSales() {
        String sql = "SELECT YEAR(created_at) AS year, SUM(total_amount) AS total_sales FROM orders WHERE status = 'done' GROUP BY YEAR(created_at)";
        Map<Integer, Double> annualSales = new HashMap<>();

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int year = rs.getInt("year");
                double totalSales = rs.getDouble("total_sales");
                annualSales.put(year, totalSales);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Proper error handling should be done here
        }

        return annualSales;
    }

    public String generateNewOrderId() {
        String lastIdQuery = "SELECT order_id FROM orders ORDER BY order_id DESC LIMIT 1";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(lastIdQuery);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                String lastId = rs.getString("order_id");
                int numericPart = Integer.parseInt(lastId.substring(2)) + 1; // Assuming the ID format is "O-XXX"
                return String.format("O-%03d", numericPart);
            } else {
                return "O-001"; // First ID if no existing records
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null; // Proper error handling should be implemented
        }
    }

    public String generateNewOrderItemId() {
        String lastIdQuery = "SELECT order_item_id FROM order_items ORDER BY order_item_id DESC LIMIT 1";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(lastIdQuery);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                String lastId = rs.getString("order_item_id");
                int numericPart = Integer.parseInt(lastId.substring(3)) + 1;
                return String.format("OI-%03d", numericPart);
            } else {
                return "OI-001";
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
