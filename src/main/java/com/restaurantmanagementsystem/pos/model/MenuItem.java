package com.restaurantmanagementsystem.pos.model;

public class MenuItem {
    private String productId;
    private String name;
    private double price;
    private String imagePath;
    private String category;
    private int stock;
    private String status;

    public MenuItem(String productId, String name, double price, String imagePath, String category, int stock, String status) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.imagePath = imagePath;
        this.category = category;
        this.stock = stock;
        this.status = status;
    }

    // Getters and Setters
    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    @Override
    public String toString() {
        return "MenuItem{" +
                "productId=" + productId +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", imagePath='" + imagePath + '\'' +
                ", category='" + category + '\'' +
                ", stock=" + stock +
                ", status='" + status + '\'' +
                '}';
    }
}

