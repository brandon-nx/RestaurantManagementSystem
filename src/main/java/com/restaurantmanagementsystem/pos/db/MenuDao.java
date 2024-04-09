package com.restaurantmanagementsystem.pos.db;

import com.restaurantmanagementsystem.pos.model.MenuItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public interface MenuDao {
    List<MenuItem> getMenuItemsFromDatabase();
    List<MenuItem> getMenuItemsFromDatabaseByCategory(String category);
}