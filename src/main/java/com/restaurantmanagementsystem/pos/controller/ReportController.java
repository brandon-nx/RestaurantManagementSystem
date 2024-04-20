package com.restaurantmanagementsystem.pos.controller;

import com.restaurantmanagementsystem.pos.db.OrderDao;
import com.restaurantmanagementsystem.pos.db.OrderDaoImpl;
import com.restaurantmanagementsystem.pos.model.DailySalesReport;
import com.restaurantmanagementsystem.pos.model.Order;
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

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ReportController {

    @FXML
    private Label todaysIncomeLabel, totalIncomeLabel, totalItemsSoldLabel, bestSellerLabel;
    @FXML
    private ComboBox<String> reportTypeComboBox;
    @FXML
    private TableView<DailySalesReport> reportTableView;
    @FXML
    private TableColumn<DailySalesReport, LocalDate> dateColumn;
    @FXML
    private TableColumn<DailySalesReport, Double> salesColumn;
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
        bindDashboardProperties();
        setupReportTypeComboBox();
        setupReportTableView();
        setupSalesBarChart();

        reportTypeComboBox.getSelectionModel().select("Daily Sales");
        populateDailySalesReport();

        // Listen for changes in ComboBox selection to update the view
        reportTypeComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                updateReportViews(newVal);
            }
        });
    }

    private void setupReportTableView() {
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        salesColumn.setCellValueFactory(new PropertyValueFactory<>("totalSales"));
        dateColumn.setCellFactory(column -> new TableCell<DailySalesReport, LocalDate>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item.format(DateTimeFormatter.ISO_LOCAL_DATE));
                }
            }
        });
    }

    private void setupSalesBarChart() {
        xAxis.setLabel("Date");
        yAxis.setLabel("Total Sales");
        yAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(yAxis, "RM", null));
    }

    private void setupReportTypeComboBox() {
        reportTypeComboBox.getItems().addAll("Daily Sales", "Monthly Sales", "Annual Sales", "Sales by Category");
    }

    private void bindDashboardProperties() {
        todaysIncomeLabel.textProperty().bind(todaysIncome);
        totalIncomeLabel.textProperty().bind(totalIncome);
        totalItemsSoldLabel.textProperty().bind(totalItemsSold);
        bestSellerLabel.textProperty().bind(bestSeller);

        loadDashboardData();
    }

    private void loadDashboardData() {
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
        // Clear existing data from TableView and BarChart
        reportTableView.getItems().clear();
        salesBarChart.getData().clear();

        // Based on the report type, call a method to update the TableView and BarChart
        switch (reportType) {
            case "Daily Sales":
                populateDailySalesReport();
                break;
            case "Monthly Sales":
                populateMonthlySalesReport();
                break;
            case "Annual Sales":
                populateAnnualSalesReport();
                break;
            case "Sales by Category":
                // populateSalesByCategoryReport();
                break;
            default:
                System.out.println("Unknown report type: " + reportType);
                break;
        }
    }

    private void populateDailySalesReport() {
        Map<LocalDate, Double> salesData = orderDao.getDailySales();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Daily Sales");
        ObservableList<DailySalesReport> tableData = FXCollections.observableArrayList();

        salesData.forEach((date, salesAmount) -> {
            series.getData().add(new XYChart.Data<>(date.format(DateTimeFormatter.ISO_LOCAL_DATE), salesAmount));
            tableData.add(new DailySalesReport(date, salesAmount));
        });

        salesBarChart.getData().add(series);
        reportTableView.setItems(tableData);
    }

    private void populateMonthlySalesReport() {
        Map<YearMonth, Double> salesData = orderDao.getMonthlySales();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Monthly Sales");
        ObservableList<DailySalesReport> tableData = FXCollections.observableArrayList();

        salesData.forEach((month, salesAmount) -> {
            String monthAsString = month.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()) + " " + month.getYear();
            series.getData().add(new XYChart.Data<>(monthAsString, salesAmount));
            tableData.add(new DailySalesReport(month.atDay(1), salesAmount));  // using the first day of the month for date
        });

        salesBarChart.getData().add(series);
        reportTableView.setItems(tableData);
    }

    private void populateAnnualSalesReport() {
        Map<Integer, Double> salesData = orderDao.getAnnualSales();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Annual Sales");
        ObservableList<DailySalesReport> tableData = FXCollections.observableArrayList();

        salesData.forEach((year, totalSales) -> {
            series.getData().add(new XYChart.Data<>(String.valueOf(year), totalSales));
            tableData.add(new DailySalesReport(LocalDate.of(year, 1, 1), totalSales));  // Adjust if using a different model for yearly data
        });

        salesBarChart.getData().add(series);
        reportTableView.setItems(tableData);
    }

}

