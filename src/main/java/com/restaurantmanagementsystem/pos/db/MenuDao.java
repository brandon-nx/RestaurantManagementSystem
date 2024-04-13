package com.restaurantmanagementsystem.pos.db;

import com.restaurantmanagementsystem.pos.model.MenuItem;
import java.util.List;

public interface MenuDao {
    List<MenuItem> getMenuItems();
    List<MenuItem> getMenuItemsByCategory(String category);
    MenuItem getMenuItemsByName(String productName);
    int getLastIdNumber();
    boolean addMenuItems(MenuItem newItem);
    boolean deleteMenuItem(MenuItem itemToDelete);

    boolean updateMenuItem(MenuItem selectedItem);
}