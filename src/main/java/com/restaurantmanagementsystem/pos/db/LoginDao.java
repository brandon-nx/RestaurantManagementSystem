package com.restaurantmanagementsystem.pos.db;

import com.restaurantmanagementsystem.pos.model.UserAuthenticationResult;

public interface LoginDao {
    UserAuthenticationResult authenticate(String username, String password);
}