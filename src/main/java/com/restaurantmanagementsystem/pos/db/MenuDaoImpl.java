package com.restaurantmanagementsystem.pos.db;

import com.restaurantmanagementsystem.pos.model.MenuItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MenuDaoImpl implements MenuDao {

    @Override
    public List<MenuItem> getMenuItems() {
        String sql = "SELECT * FROM menu_items";

        List<MenuItem> menuItems = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String productId = rs.getString("product_id");
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                String imagePath = rs.getString("image_path");
                String category = rs.getString("category");
                int stock = rs.getInt("stock");
                String status = rs.getString("status");

                menuItems.add(new MenuItem(productId, name, price, imagePath, category, stock, status));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Proper exception handling goes here
        }
        return menuItems;
    }

    @Override
    public List<MenuItem> getMenuItemsByCategory(String category) {
        List<MenuItem> menuItems = new ArrayList<>();
        String sql = "SELECT * FROM menu_items WHERE category = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, category);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String productId = rs.getString("product_id");
                    String name = rs.getString("name");
                    double price = rs.getDouble("price");
                    String imagePath = rs.getString("image_path");
                    int stock = rs.getInt("stock");
                    String status = rs.getString("status");
                    menuItems.add(new MenuItem(productId, name, price, imagePath, category, stock, status));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Proper exception handling goes here
        }
        return menuItems;
    }

    public MenuItem getMenuItemsByName(String name) {
        String sql = "SELECT * FROM menu_items WHERE name = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String productId = rs.getString("product_id");
                    String itemName = rs.getString("name");
                    double price = rs.getDouble("price");
                    String imagePath = rs.getString("image_path");
                    String category = rs.getString("category");
                    int stock = rs.getInt("stock");
                    String status = rs.getString("status");

                    return new MenuItem(productId, itemName, price, imagePath, category, stock, status);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Proper exception handling goes here
        }
        return null; // If not found or error occurred
    }

    @Override
    public boolean addMenuItems(MenuItem menuItem) {
        String sql = "INSERT INTO menu_items (product_id, name, price, image_path, category, stock, status) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, menuItem.getProductId());
            pstmt.setString(2, menuItem.getName());
            pstmt.setDouble(3, menuItem.getPrice());
            pstmt.setString(4, menuItem.getImagePath());
            pstmt.setString(5, menuItem.getCategory());
            pstmt.setInt(6, menuItem.getStock());
            pstmt.setString(7, menuItem.getStatus());

            int affectedRows = pstmt.executeUpdate();

            // Return true if one row was inserted
            return affectedRows == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            // Proper exception handling goes here
            return false;
        }
    }

    public boolean deleteMenuItem(MenuItem item) throws SQLException {
        String sql = "DELETE FROM menu_items WHERE product_id = ?";
        if (hasDependencies(item)) {
            throw new SQLException("Cannot delete this menu item because it is referenced by order items.");
        }

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, item.getProductId());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    private boolean hasDependencies(MenuItem item) {
        String sql = "SELECT COUNT(*) FROM order_items WHERE product_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, item.getProductId());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateMenuItem(MenuItem item) {
        // Replace these values with the correct table name and column names from your database schema
        String sql = "UPDATE menu_items SET name = ?, price = ?, category = ?, stock = ?, status = ? WHERE product_id = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, item.getName());
            pstmt.setDouble(2, item.getPrice());
            pstmt.setString(3, item.getCategory());
            pstmt.setInt(4, item.getStock());
            pstmt.setString(5, item.getStatus());
            pstmt.setString(6, item.getProductId()); // Make sure getProductId() returns the correct ID

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace(); // Ideally, you would have better error handling
            return false;
        }
    }

    public int getLastIdNumber() {
        String sql = "SELECT MAX(CAST(SUBSTRING(product_id FROM 3) AS INTEGER)) AS lastId FROM menu_items";
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("lastId");
            } else {
                return 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Proper exception handling goes here
        }
        return 0; // Fallback in case of an error
    }
}
