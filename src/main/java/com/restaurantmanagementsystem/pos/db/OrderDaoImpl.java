package com.restaurantmanagementsystem.pos.db;

import com.restaurantmanagementsystem.pos.model.Order;
import com.restaurantmanagementsystem.pos.model.OrderItem;
import com.restaurantmanagementsystem.pos.model.Report;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
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
    public List<Report> getDailySales(LocalDate date) {
        List<Report> reportList = new ArrayList<>();
        String sql = "SELECT category, COUNT(*) as quantity, SUM(total_amount) as sales " +
                "FROM ( " +
                "  SELECT *, " +
                "    CASE " +
                "      WHEN HOUR(created_at) BETWEEN 7 AND 11 THEN 'Breakfast' " +
                "      WHEN HOUR(created_at) BETWEEN 12 AND 17 THEN 'Lunch' " +
                "      WHEN HOUR(created_at) BETWEEN 18 AND 23 THEN 'Dinner' " +
                "      WHEN HOUR(created_at) BETWEEN 0 AND 6 THEN 'Others' " +
                "    END AS category " +
                "  FROM orders " +
                "  WHERE DATE(created_at) = ? AND status = 'done' " +
                ") AS orders_grouped " +
                "GROUP BY category " +
                "ORDER BY FIELD(category, 'Breakfast', 'Lunch', 'Dinner', 'Others');";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, Date.valueOf(date));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String category = rs.getString("category");
                    int quantity = rs.getInt("quantity");
                    double sales = rs.getDouble("sales");
                    reportList.add(new Report(category, quantity, sales));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reportList;
    }

    @Override
    public List<Report> getWeeklySales(LocalDate startOfWeek, LocalDate endOfWeek) {
        List<Report> weeklySales = new ArrayList<>();
        String sql = "SELECT DATE(created_at) AS date, SUM(total_amount) AS sales, COUNT(*) AS quantity, " +
                "DAYNAME(created_at) AS day_of_week " +
                "FROM orders " +
                "WHERE DATE(created_at) BETWEEN ? AND ? AND status = 'done' " +
                "GROUP BY date, day_of_week " +
                "ORDER BY date ASC";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, Date.valueOf(startOfWeek));
            pstmt.setDate(2, Date.valueOf(endOfWeek));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String dayOfWeek = rs.getString("day_of_week");
                    double sales = rs.getDouble("sales");
                    int quantity = rs.getInt("quantity");
                    weeklySales.add(new Report(dayOfWeek, quantity, sales));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log this exception
        }

        return weeklySales;
    }

    @Override
    public List<Report> getMonthlySales(LocalDate startOfMonth, LocalDate endOfMonth) {
        List<Report> monthlySales = new ArrayList<>();
        String sql = "SELECT DATE(created_at) AS date, SUM(total_amount) AS sales, COUNT(*) AS quantity " +
                "FROM orders " +
                "WHERE DATE(created_at) BETWEEN ? AND ? AND status = 'done' " +
                "GROUP BY date " +
                "ORDER BY date ASC";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, Date.valueOf(startOfMonth));
            pstmt.setDate(2, Date.valueOf(endOfMonth));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    LocalDate date = rs.getDate("date").toLocalDate();
                    double sales = rs.getDouble("sales");
                    int quantity = rs.getInt("quantity");
                    monthlySales.add(new Report(date.toString(), quantity, sales));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log this exception
        }

        return monthlySales;
    }

    @Override
    public List<Report> getAnnualSales(LocalDate startOfYear, LocalDate endOfYear) {
        List<Report> annualSales = new ArrayList<>();
        String sql = "SELECT MONTH(created_at) AS month, SUM(total_amount) AS sales, COUNT(*) AS quantity " +
                "FROM orders " +
                "WHERE DATE(created_at) BETWEEN ? AND ? AND status = 'done' " +
                "GROUP BY month " +
                "ORDER BY month ASC";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, Date.valueOf(startOfYear));
            pstmt.setDate(2, Date.valueOf(endOfYear));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int month = rs.getInt("month");
                    double sales = rs.getDouble("sales");
                    int quantity = rs.getInt("quantity");
                    String monthName = Month.of(month).getDisplayName(TextStyle.FULL, Locale.getDefault());
                    annualSales.add(new Report(monthName, quantity, sales));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log this exception
        }

        return annualSales;
    }

    @Override
    public List<Report> getSalesByMenuItem() {
        List<Report> menuItemSales = new ArrayList<>();
        String sql = "SELECT mi.name, SUM(oi.quantity) as quantity, SUM(oi.quantity * oi.price) as total_sales " +
                "FROM order_items oi " +
                "JOIN menu_items mi ON oi.product_id = mi.product_id " +
                "JOIN orders o ON oi.order_id = o.order_id " +
                "WHERE o.status = 'done' " +
                "GROUP BY mi.name " +
                "ORDER BY total_sales DESC";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String menuItemName = rs.getString("name");
                    int quantity = rs.getInt("quantity");
                    double sales = rs.getDouble("sales");

                    menuItemSales.add(new Report(menuItemName, quantity, sales));
                }
            }
        } catch (SQLException e) {
            // Handle exceptions properly here
            e.printStackTrace();
        }

        return menuItemSales;
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
