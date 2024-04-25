package com.restaurantmanagementsystem.pos.db;

import com.restaurantmanagementsystem.pos.model.MenuItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MenuDaoImpl implements MenuDao {
    @Override
    public boolean addMenuItems(MenuItem menuItem) {
        String sql = "INSERT INTO menu_items (product_id, name, price, image_path, category, stock, status) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String productId = generateProductId();
            menuItem.setProductId(productId);
            pstmt.setString(1, productId);
            pstmt.setString(2, menuItem.getName());
            pstmt.setDouble(3, menuItem.getPrice());
            pstmt.setString(4, menuItem.getImagePath());
            pstmt.setString(5, menuItem.getCategory());
            pstmt.setInt(6, menuItem.getStock());
            pstmt.setString(7, menuItem.getStatus());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows == 1;

        } catch (SQLException e) {
            System.err.println("Error adding menu item: " + e.getMessage());
            e.printStackTrace(System.err);
            throw new RuntimeException("Unable to add menu item. Please try again later.");
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
            System.err.println("Error checking dependencies for menu item deletion: " + e.getMessage());
            e.printStackTrace(System.err);
            throw new RuntimeException("Unable to check dependencies for menu item deletion. Please try again later.");

        }

        return false;
    }

    public boolean updateMenuItem(MenuItem item) {
        String sql = "UPDATE menu_items SET name = ?, price = ?, category = ?, stock = ?, status = ? WHERE product_id = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, item.getName());
            pstmt.setDouble(2, item.getPrice());
            pstmt.setString(3, item.getCategory());
            pstmt.setInt(4, item.getStock());
            pstmt.setString(5, item.getStatus());
            pstmt.setString(6, item.getProductId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating menu item: " + e.getMessage());
            e.printStackTrace(System.err);
            throw new RuntimeException("Unable to update menu item. Please try again later.");
        }
    }

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
            System.err.println("Error retrieving menu items: " + e.getMessage());
            e.printStackTrace(System.err);
            throw new RuntimeException("Unable to retrieve menu items. Please try again later.");
        }

        return menuItems;
    }

    @Override
    public List<String> getMenuCategories() {
        String sql = "SELECT DISTINCT category FROM menu_items";
        List<String> categories = new ArrayList<>();

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String category = rs.getString("category");
                categories.add(category);
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving menu categories: " + e.getMessage());
            e.printStackTrace(System.err);
            throw new RuntimeException("Unable to retrieve menu categories. Please try again later.");
        }

        return categories;
    }

    @Override
    public List<MenuItem> getMenuItemsByCategory(String category) {
        String sql = "SELECT * FROM menu_items WHERE category = ? AND status = 'available'";
        List<MenuItem> menuItems = new ArrayList<>();

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
            System.err.println("Error retrieving menu items by category: " + e.getMessage());
            e.printStackTrace(System.err);
            throw new RuntimeException("Unable to retrieve menu items by category. Please try again later.");
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
            System.err.println("Error retrieving menu item by name: " + e.getMessage());
            e.printStackTrace(System.err);
            throw new RuntimeException("Unable to retrieve menu item by name. Please try again later.");
        }

        throw new RuntimeException("Menu item with name '" + name + "' not found. Please try again later.");
    }

    public String generateProductId() {
        String sql = "SELECT MAX(CAST(SUBSTRING(product_id FROM 3) AS INTEGER)) AS lastId FROM menu_items";

        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            int lastIdNumber = 0;

            if (rs.next()) {
                lastIdNumber = rs.getInt("lastId");
            }

            int nextIdNumber = lastIdNumber + 1;
            return String.format("P-%03d", nextIdNumber);

        } catch (SQLException e) {
            System.err.println("Error retrieving last ID number: " + e.getMessage());
            e.printStackTrace(System.err);
            throw new RuntimeException("Unable to retrieve last ID number. Please try again later.");
        }
    }
}
