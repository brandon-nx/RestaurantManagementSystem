package com.restaurantmanagementsystem.pos.model;

import java.time.LocalDate;

public class DailySalesReport {
    private LocalDate date;
    private Double totalSales;

    public DailySalesReport(LocalDate date, Double totalSales) {
        this.date = date;
        this.totalSales = totalSales;
    }

    // Getters
    public LocalDate getDate() {
        return date;
    }

    public Double getTotalSales() {
        return totalSales;
    }

    // Setters
    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setTotalSales(Double totalSales) {
        this.totalSales = totalSales;
    }

    @Override
    public String toString() {
        return "DailySalesReport{" +
                "date=" + date +
                ", totalSales=" + totalSales +
                '}';
    }
}
