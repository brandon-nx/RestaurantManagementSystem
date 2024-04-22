package com.restaurantmanagementsystem.pos.model;

import java.time.LocalDate;

public class Report {
    private String category;
    private int quantity;
    private double sales;

    // Constructor for daily reports
    public Report(String category, int quantity, double sales) {
        this.category = category;
        this.quantity = quantity;
        this.sales = sales;
    }


    // Getters and setters
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getSales() {
        return sales;
    }

    public void setSales(double sales) {
        this.sales = sales;
    }

    // toString method for debugging purposes
    @Override
    public String toString() {
        return "Report{" +
                ", category='" + category + '\'' +
                ", quantity=" + quantity +
                ", sales=" + sales +
                '}';
    }

    public void incrementQuantity(int quantityToAdd) {
        this.quantity += quantityToAdd;
    }

    public void addToSales(double salesToAdd) {
        this.sales += salesToAdd;
    }
}
