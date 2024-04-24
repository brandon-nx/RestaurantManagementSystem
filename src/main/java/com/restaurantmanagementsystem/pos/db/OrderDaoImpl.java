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
        String sql = "INSERT INTO orders (order_id, user_id, total_amount, status) VALUES (?, ?, ?, 'pending')";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String newOrderId = generateNewOrderId();
            pstmt.setString(1, newOrderId);
            pstmt.setString(2, order.getUserId());
            pstmt.setDouble(3, order.getTotalAmount());
            pstmt.executeUpdate();
            return newOrderId;

        } catch (SQLException e) {
            System.err.println("Error adding order to database: " + e.getMessage());
            e.printStackTrace(System.err);
            throw new RuntimeException("Unable to process your order at this time. Please try again later.");
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
            System.err.println("Error adding order items to database: " + e.getMessage());
            e.printStackTrace(System.err);
            throw new RuntimeException("Unable to add order items at this time. Please try again later.");
        }
    }

    @Override
    public void updateOrderStatus(Order order) {
        String sql = "UPDATE orders SET status = ?, updated_at = NOW() WHERE order_id = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, order.getStatus());
            pstmt.setString(2, order.getOrderId());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error updating order status in the database: " + e.getMessage());
            e.printStackTrace(System.err);
            throw new RuntimeException("Unable to update order status at this time. Please try again later.");
        }
    }

    @Override
    public List<Order> getAllOrders() {
        String sql = "SELECT o.order_id, o.user_id, u.username AS customer_name, o.total_amount, o.status, o.created_at, o.updated_at " +
                "FROM orders o " +
                "JOIN users u ON o.user_id = u.user_id";
        List<Order> orders = new ArrayList<>();

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Order order = new Order();
                order.setOrderId(rs.getString("order_id"));
                order.setUserId(rs.getString("user_id"));
                order.setCustomerName(rs.getString("customer_name"));
                order.setTotalAmount(rs.getDouble("total_amount"));
                order.setStatus(rs.getString("status"));
                order.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                order.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                order.setOrderItems(getOrderItemsByOrderId(order.getOrderId()));
                orders.add(order);
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving all orders from the database: " + e.getMessage());
            e.printStackTrace(System.err);
            throw new RuntimeException("Unable to retrieve orders at this time. Please try again later.");
        }

        return orders;
    }

    @Override
    public List<Order> getOrdersByDate(LocalDate date) {
        String sql = "SELECT * FROM orders WHERE DATE(created_at) = ?";
        List<Order> orders = new ArrayList<>();

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
            System.err.println("Error retrieving orders for date " + date + ": " + e.getMessage());
            e.printStackTrace(System.err);
            throw new RuntimeException("Unable to retrieve orders for the specified date. Please try again later.");
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
            System.err.println("Error calculating total income: " + e.getMessage());
            e.printStackTrace(System.err);
            throw new RuntimeException("Unable to calculate total income. Please try again later.");
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
            System.err.println("Error calculating total items sold: " + e.getMessage());
            e.printStackTrace(System.err);
            throw new RuntimeException("Unable to calculate total items sold. Please try again later.");
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
            System.err.println("Error determining the best selling product: " + e.getMessage());
            e.printStackTrace(System.err);
            throw new RuntimeException("Unable to determine the best seller. Please try again later.");
        }

        return bestSeller;
    }

    @Override
    public List<Report> getDailySales(LocalDate date) {
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
        List<Report> reportList = new ArrayList<>();

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
            System.err.println("Error retrieving daily sales report: " + e.getMessage());
            e.printStackTrace(System.err);
            throw new RuntimeException("Unable to retrieve daily sales report. Please try again later.");
        }

        return reportList;
    }

    @Override
    public List<Report> getWeeklySales(LocalDate startOfWeek, LocalDate endOfWeek) {
        String sql = "SELECT DATE(created_at) AS date, SUM(total_amount) AS sales, COUNT(*) AS quantity, " +
                "DAYNAME(created_at) AS day_of_week " +
                "FROM orders " +
                "WHERE DATE(created_at) BETWEEN ? AND ? AND status = 'done' " +
                "GROUP BY date, day_of_week " +
                "ORDER BY date ASC";
        List<Report> weeklySales = new ArrayList<>();

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
            System.err.println("Error retrieving weekly sales report: " + e.getMessage());
            e.printStackTrace(System.err);
            throw new RuntimeException("Unable to retrieve weekly sales report. Please try again later.");
        }

        return weeklySales;
    }

    @Override
    public List<Report> getMonthlySales(LocalDate startOfMonth, LocalDate endOfMonth) {
        String sql = "SELECT DATE(created_at) AS date, SUM(total_amount) AS sales, COUNT(*) AS quantity " +
                "FROM orders " +
                "WHERE DATE(created_at) BETWEEN ? AND ? AND status = 'done' " +
                "GROUP BY date " +
                "ORDER BY date ASC";
        List<Report> monthlySales = new ArrayList<>();

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
            System.err.println("Error retrieving monthly sales report: " + e.getMessage());
            e.printStackTrace(System.err);
            throw new RuntimeException("Unable to retrieve monthly sales report. Please try again later.");
        }

        return monthlySales;
    }

    @Override
    public List<Report> getAnnualSales(LocalDate startOfYear, LocalDate endOfYear) {
        String sql = "SELECT MONTH(created_at) AS month, SUM(total_amount) AS sales, COUNT(*) AS quantity " +
                "FROM orders " +
                "WHERE DATE(created_at) BETWEEN ? AND ? AND status = 'done' " +
                "GROUP BY month " +
                "ORDER BY month ASC";
        List<Report> annualSales = new ArrayList<>();

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
            System.err.println("Error retrieving annual sales report: " + e.getMessage());
            e.printStackTrace(System.err);
            throw new RuntimeException("Unable to retrieve annual sales report. Please try again later.");
        }

        return annualSales;
    }

    @Override
    public List<Report> getSalesByMenuItem() {
        String sql = "SELECT mi.name, SUM(oi.quantity) as quantity, SUM(oi.quantity * oi.price) as sales " +
                "FROM order_items oi " +
                "JOIN menu_items mi ON oi.product_id = mi.product_id " +
                "JOIN orders o ON oi.order_id = o.order_id " +
                "WHERE o.status = 'done' " +
                "GROUP BY mi.name " +
                "ORDER BY sales DESC";
        List<Report> menuItemSales = new ArrayList<>();

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
            System.err.println("Error retrieving sales by menu item: " + e.getMessage());
            e.printStackTrace(System.err);
            throw new RuntimeException("Unable to retrieve sales by menu item. Please try again later.");
        }

        return menuItemSales;
    }

    @Override
    public List<Report> getSalesByCategory() {
        String sql = "SELECT mi.category, SUM(oi.quantity) AS quantity, SUM(oi.quantity * oi.price) AS sales " +
                "FROM order_items oi " +
                "JOIN menu_items mi ON oi.product_id = mi.product_id " +
                "JOIN orders o ON oi.order_id = o.order_id " +
                "WHERE o.status = 'done' " +
                "GROUP BY mi.category " +
                "ORDER BY sales DESC";
        List<Report> categorySales = new ArrayList<>();

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String category = rs.getString("category");
                    int quantity = rs.getInt("quantity");
                    double sales = rs.getDouble("sales");

                    categorySales.add(new Report(category, quantity, sales));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving sales by category: " + e.getMessage());
            e.printStackTrace(System.err);
            throw new RuntimeException("Unable to retrieve sales by category. Please try again later.");
        }

        return categorySales;
    }


    public String generateNewOrderId() {
        String sql = "SELECT order_id FROM orders ORDER BY order_id DESC LIMIT 1";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                String lastId = rs.getString("order_id");
                int numericPart = Integer.parseInt(lastId.substring(2)) + 1;
                return String.format("O-%03d", numericPart);
            } else {
                return "O-001";
            }

        } catch (SQLException e) {
            System.err.println("Error generating new order ID: " + e.getMessage());
            e.printStackTrace(System.err);
            throw new RuntimeException("Unable to generate new order ID. Please try again later.");
        }
    }

    public String generateNewOrderItemId() {
        String sql = "SELECT order_item_id FROM order_items ORDER BY order_item_id DESC LIMIT 1";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                String lastId = rs.getString("order_item_id");
                int numericPart = Integer.parseInt(lastId.substring(3)) + 1;
                return String.format("OI-%03d", numericPart);
            } else {
                return "OI-001";
            }

        } catch (SQLException e) {
            System.err.println("Error generating new order item ID: " + e.getMessage());
            e.printStackTrace(System.err);
            throw new RuntimeException("Unable to generate new order item ID. Please try again later.");
        }
    }

    private List<OrderItem> getOrderItemsByOrderId(String orderId) {
        String sql = "SELECT * FROM order_items WHERE order_id = ?";
        List<OrderItem> orderItems = new ArrayList<>();

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, orderId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    OrderItem item = new OrderItem();
                    item.setOrderId(orderId);
                    item.setProductName(rs.getString("product_name"));
                    item.setPrice(rs.getDouble("price"));
                    item.setQuantity(rs.getInt("quantity"));
                    orderItems.add(item);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving order items for order ID " + orderId + ": " + e.getMessage());
            e.printStackTrace(System.err);
            throw new RuntimeException("Unable to retrieve order items at this time. Please try again later.");
        }

        return orderItems;
    }
}
