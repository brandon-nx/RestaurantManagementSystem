package com.restaurantmanagementsystem.pos.model;

import java.time.LocalDate;

public class DailySalesReport {
    private LocalDate date;
    private Double totalSales;

    public DailySalesReport(LocalDate date, Double totalSales) {
        this.date = date;
        this.totalSales = totalSales;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Double getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(Double totalSales) {
        this.totalSales = totalSales;
    }
}
