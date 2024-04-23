package com.restaurantmanagementsystem.pos.model;

public class UserAuthenticationResult {
    private boolean authenticated;
    private String username;
    private String userId;
    private String userRole;

    // Constructor
    public UserAuthenticationResult(boolean authenticated, String username, String userId, String userRole) {
        this.authenticated = authenticated;
        this.username = username;
        this.userId = userId;
        this.userRole = userRole;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }
}
