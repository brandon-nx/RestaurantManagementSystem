package com.restaurantmanagementsystem.pos.db;

import com.restaurantmanagementsystem.pos.model.MenuItem;
import java.util.List;

public interface MenuDao {
    List<MenuItem> getMenuItems();
    List<MenuItem> getMenuItemsByCategory(String category);
    boolean addMenuItems(MenuItem newItem);

    int getLastIdNumber();
}