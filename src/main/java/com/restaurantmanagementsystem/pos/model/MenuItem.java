package com.restaurantmanagementsystem.pos.model;

public class MenuItem {
    private String name;
    private double price;
    private String imagePath;
    private String category;

    public MenuItem(String name, double price, String imagePath, String category) {
        this.name = name;
        this.price = price;
        this.imagePath = imagePath;
        this.category = category;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}

