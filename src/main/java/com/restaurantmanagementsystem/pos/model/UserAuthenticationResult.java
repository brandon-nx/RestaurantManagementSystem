package com.restaurantmanagementsystem.pos.model;

public class UserAuthenticationResult {
    private final boolean authenticated;
    private final String userRole;
    private final String userId;

    public UserAuthenticationResult(boolean authenticated, String userRole, String userId) {
        this.authenticated = authenticated;
        this.userRole = userRole;
        this.userId = userId;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public String getUserRole() {
        return userRole;
    }

    public String getUserId() {
        return userId;
    }
}
