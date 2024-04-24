package com.restaurantmanagementsystem.pos.controller;

import com.restaurantmanagementsystem.pos.db.MenuDao;
import com.restaurantmanagementsystem.pos.db.MenuDaoImpl;
import com.restaurantmanagementsystem.pos.db.OrderDao;
import com.restaurantmanagementsystem.pos.db.OrderDaoImpl;
import com.restaurantmanagementsystem.pos.model.MenuItem;
import com.restaurantmanagementsystem.pos.model.Order;
import com.restaurantmanagementsystem.pos.model.Report;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

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
    DatePicker datePicker;
    @FXML
    ComboBox<String> reportTypeComboBox;
    @FXML
    TableView<Report> reportTableView;
    @FXML
    TableColumn<Report, String> categoryColumn;
    @FXML
    TableColumn<Report, Number> quantityColumn;
    @FXML
    TableColumn<Report, Number> salesColumn;
    @FXML
    VBox chartContainer;

    private final OrderDao orderDao = new OrderDaoImpl();
    private final MenuDao menuDao = new MenuDaoImpl();

    @FXML
    public void initialize() {
        setupReportTypeComboBox();
        setupDatePicker();
        setupCellValueFactories();
        loadInitialData(LocalDate.now());
    }

    private void setupReportTypeComboBox() {
        List<String> reportTypes = Arrays.asList("Daily Sales Report", "Weekly Sales Report", "Monthly Sales Report",
                "Annual Sales Report", "Sales by Menu Item", "Sales by Category");
        reportTypeComboBox.getItems().addAll(reportTypes);
        reportTypeComboBox.getSelectionModel().selectFirst();
        reportTypeComboBox.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> updateReportViews(newVal, datePicker.getValue())
        );
    }

    private void setupDatePicker() {
        datePicker.setValue(LocalDate.now());
        datePicker.setOnAction(event -> updateReportViews(reportTypeComboBox.getValue(), datePicker.getValue()));
    }

    private void setupCellValueFactories() {
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        salesColumn.setCellValueFactory(new PropertyValueFactory<>("sales"));
    }

    private void loadInitialData(LocalDate date) {
        updateDashboardData(date);
        updateReportViews(reportTypeComboBox.getSelectionModel().getSelectedItem(), date);
    }

    private void updateDashboardData(LocalDate date) {
        List<Order> ordersToday = orderDao.getOrdersByDate(date);
        double totalIncomeToday = ordersToday.stream()
                .filter(order -> "done".equals(order.getStatus()))
                .mapToDouble(Order::getTotalAmount)
                .sum();
        setDashboardData(totalIncomeToday, orderDao.getTotalIncome(), orderDao.getTotalItemsSold(), orderDao.getBestSeller());
    }

    private void setDashboardData(double todayIncome, double totalIncome, int totalItems, String bestSeller) {
        todaysIncomeLabel.setText(String.format("RM %.2f", todayIncome));
        totalIncomeLabel.setText(String.format("RM %.2f", totalIncome));
        totalItemsSoldLabel.setText(String.valueOf(totalItems));
        bestSellerLabel.setText(bestSeller);
    }

    private void updateReportViews(String reportType, LocalDate selectedDate) {
        List<Report> reports = fetchReports(reportType, selectedDate);
        reportTableView.setItems(FXCollections.observableArrayList(reports));
        createOrUpdateBarChart(reportType, reports);
    }

    private List<Report> fetchReports(String reportType, LocalDate selectedDate) {
        switch (reportType) {
            case "Daily Sales Report":
                List<Report> dailyCategories = initializeDailyCategories();
                List<Report> dailySales = orderDao.getDailySales(selectedDate);
                return combineCategoriesWithSales(dailyCategories, dailySales);

            case "Weekly Sales Report":
                DateRange weekRange = getStartAndEndOfWeek(selectedDate);
                List<Report> weeklyCategories = initializeWeeklyCategories(selectedDate);
                List<Report> weeklySales = orderDao.getWeeklySales(weekRange.start, weekRange.end);
                return combineCategoriesWithSales(weeklyCategories, weeklySales);

            case "Monthly Sales Report":
                DateRange monthRange = getStartAndEndOfMonth(selectedDate);
                List<Report> monthlyCategories = initializeMonthlyCategories(selectedDate);
                List<Report> monthlySales = orderDao.getMonthlySales(monthRange.start, monthRange.end);
                return combineCategoriesWithSales(monthlyCategories, monthlySales);

            case "Annual Sales Report":
                List<Report> annualCategories = initializeAnnualCategories(selectedDate);
                DateRange yearRange = getStartAndEndOfYear(selectedDate);
                List<Report> annualSales = orderDao.getAnnualSales(yearRange.start, yearRange.end);
                return combineCategoriesWithSales(annualCategories, annualSales);

            case "Sales by Menu Item":
                List<Report> menuItemCategories = initializeByMenuItems();
                List<Report> salesByMenuItem = orderDao.getSalesByMenuItem();
                return combineCategoriesWithSales(menuItemCategories, salesByMenuItem);

            case "Sales by Category":
                List<Report> categoryCategories = initializeByCategories();
                List<Report> salesByCategory = orderDao.getSalesByCategory();
                return combineCategoriesWithSales(categoryCategories, salesByCategory);

            default:
                System.out.println("Unknown report type: " + reportType);
                return new ArrayList<>();
        }
    }

    private DateRange getStartAndEndOfWeek(LocalDate date) {
        LocalDate startOfWeek = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        return new DateRange(startOfWeek, endOfWeek);
    }

    private DateRange getStartAndEndOfMonth(LocalDate date) {
        LocalDate startOfMonth = date.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endOfMonth = date.with(TemporalAdjusters.lastDayOfMonth());
        return new DateRange(startOfMonth, endOfMonth);
    }

    private DateRange getStartAndEndOfYear(LocalDate date) {
        LocalDate startOfYear = date.with(TemporalAdjusters.firstDayOfYear());
        LocalDate endOfYear = date.with(TemporalAdjusters.lastDayOfYear());
        return new DateRange(startOfYear, endOfYear);
    }

    private static class DateRange {
        final LocalDate start;
        final LocalDate end;

        DateRange(LocalDate start, LocalDate end) {
            this.start = start;
            this.end = end;
        }
    }

    private List<Report> initializeDailyCategories() {
        List<Report> reports = new ArrayList<>();
        String[] categoryNames = {"Breakfast", "Lunch", "Dinner", "Others"};

        for (String categoryName : categoryNames) {
            reports.add(new Report(categoryName, 0, 0.0));
        }

        return reports;
    }

    private List<Report> initializeWeeklyCategories(LocalDate date) {
        List<Report> reports = new ArrayList<>();
        LocalDate startOfWeek = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        for (int dayIndex = 0; dayIndex < 7; dayIndex++) {
            LocalDate specificDay = startOfWeek.plusDays(dayIndex);
            String dayName = specificDay.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
            Report report = new Report(dayName, 0, 0.0);
            reports.add(report);
        }

        return reports;
    }

    private List<Report> initializeMonthlyCategories(LocalDate date) {
        List<Report> reports = new ArrayList<>();
        LocalDate startOfMonth = date.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endOfMonth = date.with(TemporalAdjusters.lastDayOfMonth());

        while (!startOfMonth.isAfter(endOfMonth)) {
            String formattedDate = startOfMonth.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            Report report = new Report(formattedDate, 0, 0.0);
            reports.add(report);
            startOfMonth = startOfMonth.plusDays(1);
        }

        return reports;
    }

    private List<Report> initializeAnnualCategories(LocalDate date) {
        List<Report> reports = new ArrayList<>();

        for (int monthIndex = 1; monthIndex <= 12; monthIndex++) {
            Month month = Month.of(monthIndex);
            String monthName = month.getDisplayName(TextStyle.FULL, Locale.getDefault());
            Report report = new Report(monthName, 0, 0.0);
            reports.add(report);
        }

        return reports;
    }

    private List<Report> initializeByMenuItems() {
        List<MenuItem> menuItems = menuDao.getMenuItems();
        List<Report> reports = new ArrayList<>();

        for (MenuItem menuItem : menuItems) {
            Report report = new Report(menuItem.getName(), 0, 0.0);
            reports.add(report);
        }

        return reports;
    }

    private List<Report> initializeByCategories() {
        List<String> categoryNames = menuDao.getMenuCategories();
        List<Report> reports = new ArrayList<>();

        for (String category : categoryNames) {
            Report report = new Report(category, 0, 0.0);
            reports.add(report);
        }

        return reports;
    }


    private List<Report> combineCategoriesWithSales(List<Report> categories, List<Report> sales) {
        for (Report category : categories) {
            for (Report sale : sales) {
                if (category.getCategory().equals(sale.getCategory())) {
                    category.setQuantity(sale.getQuantity());
                    category.setSales(sale.getSales());
                    break;
                }
            }
        }
        return categories;
    }

    private void createOrUpdateBarChart(String reportType, List<Report> reports) {
        chartContainer.getChildren().clear();

        BarChart<String, Number> barChart = new BarChart<>(new CategoryAxis(), new NumberAxis());
        barChart.setTitle(reportType);
        barChart.setLegendVisible(false);
        barChart.getXAxis().setLabel("Categories");
        barChart.getYAxis().setLabel("Sales (RM)");

        XYChart.Series<String, Number> series = new XYChart.Series<>();

        for (Report report : reports) {
            series.getData().add(new XYChart.Data<>(report.getCategory(), report.getSales()));
        }

        barChart.getData().add(series);
        chartContainer.getChildren().add(barChart);
    }
}