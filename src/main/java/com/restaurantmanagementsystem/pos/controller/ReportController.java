package com.restaurantmanagementsystem.pos.controller;

import com.restaurantmanagementsystem.pos.db.OrderDao;
import com.restaurantmanagementsystem.pos.db.OrderDaoImpl;
import com.restaurantmanagementsystem.pos.model.Order;
import com.restaurantmanagementsystem.pos.model.Report;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;

import java.util.*;

public class ReportController {
    @FXML
    private Label todaysIncomeLabel, totalIncomeLabel, totalItemsSoldLabel, bestSellerLabel;
    @FXML
    public DatePicker datePicker;
    @FXML
    private ComboBox<String> reportTypeComboBox;
    @FXML
    private TableView<Report> reportTableView;
    @FXML
    private TableColumn<Report, String> categoryColumn;
    @FXML
    private TableColumn<Report, Number> quantityColumn;
    @FXML
    private TableColumn<Report, Number> salesColumn;
    @FXML
    private BarChart<String, Number> salesBarChart;

    private final OrderDao orderDao = new OrderDaoImpl();
    private final SimpleStringProperty todaysIncome = new SimpleStringProperty();
    private final SimpleStringProperty totalIncome = new SimpleStringProperty();
    private final SimpleStringProperty totalItemsSold = new SimpleStringProperty();
    private final SimpleStringProperty bestSeller = new SimpleStringProperty();

    @FXML
    public void initialize() {
        setupDashboardData();
        setupReportTypeComboBox();
        setupDatePicker();
        setCellValueFactories();
        updateReportViews("Daily Sales Report", datePicker.getValue());
        reportTypeComboBox.getSelectionModel().select("Daily Sales Report");
        reportTypeComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            updateReportViews(newVal, datePicker.getValue());
        });
    }

    private void setupDatePicker() {
        datePicker.setValue(LocalDate.now());
        datePicker.setOnAction(event -> {
            LocalDate selectedDate = datePicker.getValue();
            if (selectedDate != null) {
                String reportType = reportTypeComboBox.getValue();
                updateReportViews(reportType, selectedDate);
            }
        });
    }

    private void setCellValueFactories() {
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        salesColumn.setCellValueFactory(new PropertyValueFactory<>("sales"));
    }

    private void setupReportTypeComboBox() {
        reportTypeComboBox.getItems().addAll(
                "Daily Sales Report",
                "Weekly Sales Report",
                "Monthly Sales Report",
                "Annual Sales Report",
                "Sales by Menu Item",
                "Sales by Category");
    }

    private void setupDashboardData() {
        todaysIncomeLabel.textProperty().bind(todaysIncome);
        totalIncomeLabel.textProperty().bind(totalIncome);
        totalItemsSoldLabel.textProperty().bind(totalItemsSold);
        bestSellerLabel.textProperty().bind(bestSeller);

        calculateTodaysIncome();
        calculateTotalIncome();
        calculateTotalItemsSold();
        calculateBestSeller();
    }

    // Calculate today's income from completed orders.
    private void calculateTodaysIncome() {
        LocalDate today = LocalDate.now();
        List<Order> ordersToday = orderDao.getOrdersByDate(today);

        double income = ordersToday.stream()
                .filter(order -> "done".equals(order.getStatus())) // assuming 'completed' is a status
                .mapToDouble(Order::getTotalAmount)
                .sum();

        todaysIncome.set(String.format("RM%.2f", income));
    }

    // Calculate total income from all orders.
    private void calculateTotalIncome() {
        double income = orderDao.getTotalIncome();
        totalIncome.set(String.format("RM%.2f", income));
    }

    // Calculate total items sold.
    private void calculateTotalItemsSold() {
        int itemsSold = orderDao.getTotalItemsSold();
        totalItemsSold.set(String.valueOf(itemsSold));
    }

    // Determine the best selling product.
    private void calculateBestSeller() {
        String bestSellingProduct = orderDao.getBestSeller();
        bestSeller.set(bestSellingProduct);
    }

    // Update the report views and sales bar chart based on the selected report type and date.
    private void updateReportViews(String reportType, LocalDate selectedDate) {
        reportTableView.getItems().clear();
        salesBarChart.getData().clear();

        switch (reportType) {
            case "Daily Sales Report":
                populateDailySalesReport(selectedDate);
                break;
            case "Weekly Sales Report":
                populateWeeklySalesReport(selectedDate);
                break;
            case "Monthly Sales Report":
                populateMonthlySalesReport(selectedDate);
                break;
            case "Annual Sales Report":
                populateAnnualSalesReport(selectedDate);
                break;
            case "Sales by Menu Item":
                populateSalesByMenuItemReport();
                break;
            case "Sales by Category":
                populateSalesByCategoryReport();
                break;
            default:
                System.out.println("Unknown report type: " + reportType);
                break;
        }
    }

    // Updates the table view and bar chart with the given sales data.
    private void updateUIComponents(Collection<Report> salesData) {
        reportTableView.setItems(FXCollections.observableArrayList(salesData));
        updateSalesBarChart(salesData);
    }

    private void updateSalesBarChart(Collection<Report> salesData) {
        XYChart.Series<String, Number> salesSeries = new XYChart.Series<>();

        for (Report sale : salesData) {
            salesSeries.getData().add(new XYChart.Data<>(sale.getCategory(), sale.getSales()));
        }

        salesBarChart.getData().setAll(salesSeries);
    }

    // Aggregate sales data for each report type
    private void aggregateSalesData(List<Report> salesData, Map<String, Report> dataMap) {
        for (Report sale : salesData) {
            Report existingData = dataMap.get(sale.getCategory());
            if (existingData != null) {
                existingData.incrementQuantity(sale.getQuantity());
                existingData.addToSales(sale.getSales());
            }
        }
    }

    // Initialise all the data for each report type
    private Map<String, Report> initializeDailyData() {
        Map<String, Report> map = new LinkedHashMap<>();
        String[] mealTimes = {"Breakfast", "Lunch", "Dinner", "Others"};
        for (String mealTime : mealTimes) {
            map.put(mealTime, new Report(mealTime, 0, 0.0));
        }

        return map;
    }

    private Map<String, Report> initializeWeeklyData(LocalDate startOfWeek) {
        Map<String, Report> map = new LinkedHashMap<>();
        LocalDate date = startOfWeek;
        for (int i = 0; i < 7; i++) {
            String dayName = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
            map.put(dayName, new Report(dayName, 0, 0.0));
            date = date.plusDays(1);
        }
        return map;
    }

    private Map<String, Report> initializeMonthlyData(LocalDate startOfMonth) {
        Map<String, Report> map = new LinkedHashMap<>();
        LocalDate date = startOfMonth;
        LocalDate endOfMonth = startOfMonth.with(TemporalAdjusters.lastDayOfMonth());

        while (!date.isAfter(endOfMonth)) {
            String dayName = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            map.put(dayName, new Report(dayName, 0, 0.0));
            date = date.plusDays(1);
        }

        return map;
    }

    private Map<String, Report> initializeAnnualData() {
        Map<String, Report> map = new LinkedHashMap<>();
        for (int i = 1; i <= 12; i++) {
            String monthName = Month.of(i).getDisplayName(TextStyle.FULL, Locale.getDefault());
            map.put(monthName, new Report(monthName, 0, 0.0));
        }
        return map;
    }

    private Map<String, Report> initializeMenuItemData(List<Report> menuItemSalesData) {
        Map<String, Report> menuItemDataMap = new LinkedHashMap<>();
        for (Report item : menuItemSalesData) {
            menuItemDataMap.put(item.getCategory(), new Report(item.getCategory(), 0, 0.0));
        }
        return menuItemDataMap;
    }

    private Map<String, Report> initializeCategoryData(List<Report> categorySalesData) {
        Map<String, Report> categoryDataMap = new LinkedHashMap<>();
        for (Report item : categorySalesData) {
            categoryDataMap.putIfAbsent(item.getCategory(), new Report(item.getCategory(), 0, 0.0));
            categoryDataMap.get(item.getCategory()).incrementQuantity(item.getQuantity());
            categoryDataMap.get(item.getCategory()).addToSales(item.getSales());
        }
        return categoryDataMap;
    }

    // Populates all the data for each report type.
    private void populateDailySalesReport(LocalDate date) {
        List<Report> dailySalesData = orderDao.getDailySales(date);
        Map<String, Report> completeDailyData = initializeDailyData();

        aggregateSalesData(dailySalesData, completeDailyData);
        updateUIComponents(completeDailyData.values());
    }

    private void populateWeeklySalesReport(LocalDate date) {
        LocalDate startOfWeek = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        List<Report> weeklySalesData = orderDao.getWeeklySales(startOfWeek, endOfWeek);
        Map<String, Report> completeWeeklyData = initializeWeeklyData(startOfWeek);

        aggregateSalesData(weeklySalesData, completeWeeklyData);
        updateUIComponents(completeWeeklyData.values());
    }

    private void populateMonthlySalesReport(LocalDate date) {
        LocalDate startOfMonth = date.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endOfMonth = date.with(TemporalAdjusters.lastDayOfMonth());

        List<Report> monthlySalesData = orderDao.getMonthlySales(startOfMonth, endOfMonth);
        Map<String, Report> completeMonthlyData = initializeMonthlyData(startOfMonth);

        aggregateSalesData(monthlySalesData, completeMonthlyData);
        updateUIComponents(completeMonthlyData.values());
    }

    private void populateAnnualSalesReport(LocalDate date) {
        LocalDate startOfYear = date.with(TemporalAdjusters.firstDayOfYear());
        LocalDate endOfYear = date.with(TemporalAdjusters.lastDayOfYear());

        List<Report> annualSalesData = orderDao.getAnnualSales(startOfYear, endOfYear);
        Map<String, Report> completeAnnualData = initializeAnnualData();

        aggregateSalesData(annualSalesData, completeAnnualData);
        updateUIComponents(completeAnnualData.values());
    }

    private void populateSalesByMenuItemReport() {
        List<Report> menuItemSalesData = orderDao.getSalesByMenuItem();
        Map<String, Report> completeMenuItemData = initializeMenuItemData(menuItemSalesData);

        aggregateSalesData(menuItemSalesData, completeMenuItemData);
        updateUIComponents(completeMenuItemData.values());
    }

    private void populateSalesByCategoryReport() {
        List<Report> categorySalesData = orderDao.getSalesByCategory();
        Map<String, Report> completeCategoryData = initializeCategoryData(categorySalesData);

        aggregateSalesData(categorySalesData, completeCategoryData);
        updateUIComponents(completeCategoryData.values());
    }
}

