package com.restaurantmanagementsystem.pos.db;

import com.restaurantmanagementsystem.pos.model.Order;
import com.restaurantmanagementsystem.pos.model.OrderItem;
import com.restaurantmanagementsystem.pos.model.Report;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

public interface OrderDao {
    String addOrder(Order order);
    void addOrderItems(List<OrderItem> orderItems, String orderId);

    void updateOrderStatus(Order order);

    List<Order> getAllOrders();
    List<Order> getOrdersByDate(LocalDate date);
    double getTotalIncome();
    int getTotalItemsSold();
    String getBestSeller();
    List<Report> getDailySales(LocalDate date);
    List<Report> getWeeklySales(LocalDate startOfWeek, LocalDate endOfWeek);
    List<Report> getMonthlySales(LocalDate startOfMonth, LocalDate endOfMonth);
    List<Report> getAnnualSales(LocalDate startOfYear, LocalDate endOfYear);
    List<Report> getSalesByMenuItem();


    String generateNewOrderId();
}
