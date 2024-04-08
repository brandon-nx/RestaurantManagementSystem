package com.restaurantmanagementsystem.pos.db;

import com.restaurantmanagementsystem.pos.model.MenuItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MenuDaoImpl implements MenuDao {
    @Override
    public List<MenuItem> getMenuItemsFromDatabase() {
        List<MenuItem> menuItems = new ArrayList<>();
        String sql = "SELECT name, price, image_path, category FROM menu_items";

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
}
