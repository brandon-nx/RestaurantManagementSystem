package com.restaurantmanagementsystem.pos.controller;

import com.restaurantmanagementsystem.pos.db.OrderDao;
import com.restaurantmanagementsystem.pos.db.OrderDaoImpl;
import com.restaurantmanagementsystem.pos.model.Order;
import com.restaurantmanagementsystem.pos.model.Report;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
    private ComboBox<String> reportTypeComboBox;
    @FXML
    private TableView<Report> reportTableView;
    @FXML
    public TableColumn<Report, String> categoryColumn;
    @FXML
    public TableColumn<Report, Number> quantityColumn;
    @FXML
    public TableColumn<Report, Number> salesColumn;


    @FXML
    private BarChart<String, Number> salesBarChart;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;

    private OrderDao orderDao = new OrderDaoImpl();
    private SimpleStringProperty todaysIncome = new SimpleStringProperty();
    private SimpleStringProperty totalIncome = new SimpleStringProperty();
    private SimpleStringProperty totalItemsSold = new SimpleStringProperty();
    private SimpleStringProperty bestSeller = new SimpleStringProperty();


    @FXML
    public void initialize() {
        setupDashboardData();
        setupReportTypeComboBox();
        setCellValueFactory();
        updateReportViews("Daily Sales Report");

        reportTypeComboBox.getSelectionModel().select("Daily Sales Report");
        reportTypeComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                updateReportViews(newVal);
            }
        });
    }

    private void setCellValueFactory() {
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

    private void calculateTodaysIncome() {
        LocalDate today = LocalDate.now();
        List<Order> ordersToday = orderDao.getOrdersByDate(today);

        double income = ordersToday.stream()
                .filter(order -> "done".equals(order.getStatus())) // assuming 'completed' is a status
                .mapToDouble(Order::getTotalAmount)
                .sum();

        todaysIncome.set(String.format("RM%.2f", income));
    }

    private void calculateTotalIncome() {
        double income = orderDao.getTotalIncome();
        totalIncome.set(String.format("RM%.2f", income));
    }

    private void calculateTotalItemsSold() {
        int itemsSold = orderDao.getTotalItemsSold();
        totalItemsSold.set(String.valueOf(itemsSold));
    }

    private void calculateBestSeller() {
        String bestSellingProduct = orderDao.getBestSeller();
        bestSeller.set(bestSellingProduct);
    }

    private void updateReportViews(String reportType) {
        reportTableView.getItems().clear();
        salesBarChart.getData().clear();

        switch (reportType) {
            case "Daily Sales Report":
                populateDailySalesReport();
                break;
            case "Weekly Sales Report":
                populateWeeklySalesReport();
                break;
            case "Monthly Sales Report":
                populateMonthlySalesReport();
                break;
            case "Annual Sales Report":
                populateAnnualSalesReport();
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

    private void updateSalesBarChart(Collection<Report> salesData) {
        XYChart.Series<String, Number> salesSeries = new XYChart.Series<>();

        for (Report sale : salesData) {
            salesSeries.getData().add(new XYChart.Data<>(sale.getCategory(), sale.getSales()));
        }

        salesBarChart.getData().setAll(salesSeries);
    }

    // Daily Sales Report
    private void populateDailySalesReport() {
        LocalDate today = LocalDate.of(2024, 4, 19);
        List<Report> todayReport = orderDao.getDailySales(today);

        Map<String, Report> completeTodayReport = getCompleteSalesData(todayReport);

        reportTableView.setItems(FXCollections.observableArrayList(completeTodayReport.values()));
        updateSalesBarChart(completeTodayReport.values());
    }

    private Map<String, Report> getCompleteSalesData(List<Report> salesData) {
        String[] mealTimes = {"Breakfast", "Lunch", "Dinner", "Others"};

        Map<String, Report> completeData = new LinkedHashMap<>();
        for (String mealTime : mealTimes) {
            completeData.put(mealTime, new Report(mealTime, 0, 0.0));
        }

        for (Report sale : salesData) {
            Report existingData = completeData.get(sale.getCategory());
            existingData.incrementQuantity(sale.getQuantity());
            existingData.addToSales(sale.getSales());
        }

        return completeData;
    }

    private void populateWeeklySalesReport() {
        LocalDate today = LocalDate.of(2024, 4, 15);
        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        List<Report> weeklySalesData = orderDao.getWeeklySales(startOfWeek, endOfWeek);
        Map<String, Report> completeWeeklyData = initializeWeeklyData(startOfWeek);

        for (Report dailySale : weeklySalesData) {
           if (completeWeeklyData.containsKey(dailySale.getCategory())) {
                Report report = completeWeeklyData.get(dailySale.getCategory());
                report.setQuantity(dailySale.getQuantity());
                report.setSales(dailySale.getSales());
           }
        }

        reportTableView.setItems(FXCollections.observableArrayList(completeWeeklyData.values()));
        updateSalesBarChart(completeWeeklyData.values());
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

    private void populateMonthlySalesReport() {
        LocalDate startOfMonth = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endOfMonth = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());

        List<Report> monthlySalesData = orderDao.getMonthlySales(startOfMonth, endOfMonth);
        Map<String, Report> completeMonthlyData = initializeMonthlyData(startOfMonth);

        for (Report dailySale : monthlySalesData) {
            if (completeMonthlyData.containsKey(dailySale.getCategory())) {
                Report report = completeMonthlyData.get(dailySale.getCategory());
                report.setQuantity(dailySale.getQuantity());
                report.setSales(dailySale.getSales());
            }
        }

        // Update the table view and bar chart for the monthly report
        reportTableView.setItems(FXCollections.observableArrayList(completeMonthlyData.values()));
        updateSalesBarChart(completeMonthlyData.values());
    }

    private Map<String, Report> initializeMonthlyData(LocalDate startOfMonth) {
        Map<String, Report> map = new LinkedHashMap<>();
        LocalDate date = startOfMonth;
        while (!date.isAfter(startOfMonth.with(TemporalAdjusters.lastDayOfMonth()))) {
            String dayName = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            map.put(dayName, new Report(dayName, 0, 0.0));
            date = date.plusDays(1);
        }
        return map;
    }

    private void populateAnnualSalesReport() {
        LocalDate startOfYear = LocalDate.now().with(TemporalAdjusters.firstDayOfYear());
        LocalDate endOfYear = LocalDate.now().with(TemporalAdjusters.lastDayOfYear());

        List<Report> annualSalesData = orderDao.getAnnualSales(startOfYear, endOfYear);
        Map<String, Report> completeAnnualData = initializeAnnualData();

        for (Report monthlySale : annualSalesData) {
            if (completeAnnualData.containsKey(monthlySale.getCategory())) {
                Report report = completeAnnualData.get(monthlySale.getCategory());
                report.setQuantity(monthlySale.getQuantity());
                report.setSales(monthlySale.getSales());
            }
        }

        reportTableView.setItems(FXCollections.observableArrayList(completeAnnualData.values()));
        updateSalesBarChart(completeAnnualData.values());
    }

    private Map<String, Report> initializeAnnualData() {
        Map<String, Report> map = new LinkedHashMap<>();
        for (int i = 1; i <= 12; i++) {
            String monthName = Month.of(i).getDisplayName(TextStyle.FULL, Locale.getDefault());
            map.put(monthName, new Report(monthName, 0, 0.0));
        }
        return map;
    }

    private void populateSalesByMenuItemReport() {
        // Fetch the sales data for each menu item
        List<Report> menuItemSalesData = orderDao.getSalesByMenuItem();

        // Clear existing data in the UI components
        reportTableView.getItems().clear();
        salesBarChart.getData().clear();

        // Prepare a new series for the BarChart
        XYChart.Series<String, Number> salesSeries = new XYChart.Series<>();
        salesSeries.setName("Menu Item Sales");

        // Populate the table and the chart with new data
        for (Report report : menuItemSalesData) {
            // Add data to the TableView
            reportTableView.getItems().add(report);

            // Create a new chart data point for each menu item and add it to the series
            XYChart.Data<String, Number> chartData = new XYChart.Data<>(report.getCategory(), report.getSales());
            salesSeries.getData().add(chartData);
        }

        // Add the series to the BarChart
        salesBarChart.getData().add(salesSeries);

        // Update layout to reflect new data
        Platform.runLater(() -> salesBarChart.layout());
    }


    private void populateSalesByCategoryReport() {
        // TODO: Populate the report for sales by category
    }
}

