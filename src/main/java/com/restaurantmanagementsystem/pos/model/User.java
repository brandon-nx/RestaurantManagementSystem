package com.restaurantmanagementsystem.pos.model;

public class User {
    private String userId;
    private String username;
    private String password;
    private String role;
    private String contact;
    private String address;

    // Constructor
    public User(String userId, String username, String password, String role, String contact, String address) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
        this.contact = contact;
        this.address = address;
    }

    // Gettersand Setters
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }

    public String getContact() {
        return contact;
    }
    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    // toString method for debugging
    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                ", contact='" + contact + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}

