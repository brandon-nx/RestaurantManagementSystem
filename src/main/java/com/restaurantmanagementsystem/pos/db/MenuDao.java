package com.restaurantmanagementsystem.pos.db;

import com.restaurantmanagementsystem.pos.model.MenuItem;

import java.sql.SQLException;
import java.util.List;

public interface MenuDao {
    boolean addMenuItems(MenuItem newItem);

    boolean deleteMenuItem(MenuItem item) throws SQLException;

    boolean updateMenuItem(MenuItem selectedItem);

    List<MenuItem> getMenuItems();

    List<String> getMenuCategories();

    List<MenuItem> getMenuItemsByCategory(String category);
    MenuItem getMenuItemsByName(String productName);
}