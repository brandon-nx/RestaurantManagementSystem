package com.restaurantmanagementsystem.pos.db;

public interface RegisterDao {
    boolean registerCustomer(String username, String password, String contact, String address);
    boolean checkUsernameExists(String username);
}
