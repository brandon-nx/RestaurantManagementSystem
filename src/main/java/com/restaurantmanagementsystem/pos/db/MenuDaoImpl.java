package com.restaurantmanagementsystem.pos.db;

import com.restaurantmanagementsystem.pos.model.MenuItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MenuDaoImpl implements MenuDao {

    @Override
    public List<MenuItem> getMenuItemsFromDatabase() {
        String sql = "SELECT * FROM menu_items";

        List<MenuItem> menuItems = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                String imagePath = rs.getString("image_path");
                String category = rs.getString("category");
                menuItems.add(new MenuItem(name, price, imagePath, category));

            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Proper exception handling goes here
        }
        return menuItems;
    }

    @Override
    public List<MenuItem> getMenuItemsFromDatabaseByCategory(String category) {
        List<MenuItem> menuItems = new ArrayList<>();
        String sql = "SELECT * FROM menu_items WHERE category = ?";
        //System.out.println("Querying category: " + category);

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, category);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString("name");
                    double price = rs.getDouble("price");
                    String imagePath = rs.getString("image_path");
                    String itemCategory = rs.getString("category");
                    menuItems.add(new MenuItem(name, price, imagePath, itemCategory));
                    //System.out.println("Found item: " + name + " | Price: " + price + " | Image Path: " + imagePath);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Proper exception handling goes here
        }
        return menuItems;
    }
}
