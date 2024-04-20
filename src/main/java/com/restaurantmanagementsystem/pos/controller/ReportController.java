package com.restaurantmanagementsystem.pos.controller;

import com.restaurantmanagementsystem.pos.db.MenuDao;
import com.restaurantmanagementsystem.pos.db.MenuDaoImpl;
import com.restaurantmanagementsystem.pos.db.OrderDao;
import com.restaurantmanagementsystem.pos.db.OrderDaoImpl;
import com.restaurantmanagementsystem.pos.model.DailySalesReport;
import com.restaurantmanagementsystem.pos.model.Order;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;


public class ReportController {

    @FXML
    private ComboBox<String> reportTypeComboBox;
    @FXML
    private DatePicker startDatePicker, endDatePicker;
    @FXML
    private TextField categoryFilter;
    @FXML
    private TableView<Object> reportTableView;
    @FXML
    private TableColumn<Order, String> orderIdColumn;
    @FXML
    private TableColumn<Order, Double> totalAmountColumn;
    @FXML
    private TableColumn<Order, String> statusColumn;
    @FXML
    private TableColumn<Order, LocalDateTime> createdAtColumn;
    @FXML
    private BarChart<String, Number> salesBarChart;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;
    private OrderDao orderDao = new OrderDaoImpl();

    @FXML
    public void initialize() {
        setupReportTypeComboBox();
        populateReports("Sales by Date");
    }

    private void setupReportTypeComboBox() {
        reportTypeComboBox.getItems().addAll("Sales by Date", "Inventory Level", "Popular Items");
        reportTypeComboBox.getSelectionModel().selectFirst();
        reportTypeComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            populateReports(newVal);
        });
    }

    private void populateReports(String reportType) {
        reportTableView.getItems().clear();
        salesBarChart.getData().clear();

        // Fetch data based on report type
        switch (reportType) {
            case "Sales by Date":
                configureSalesByDateColumns();
                populateSalesByDate();
                break;
            case "Inventory Level":
                configureInventoryLevelColumns();
                populateInventoryLevel();
                break;
            case "Popular Items":
                configurePopularItemsColumns();
                populatePopularItems();
                break;
            default:
                break;
        }
    }

    private void configureSalesByDateColumns() {
        reportTableView.getColumns().clear(); // Clear existing columns

        TableColumn<Object, LocalDate> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
                ((DailySalesReport) cellData.getValue()).getDate()));

        TableColumn<Object, Double> totalSalesColumn = new TableColumn<>("Total Sales");
        totalSalesColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
                ((DailySalesReport) cellData.getValue()).getTotalSales()));

        reportTableView.getColumns().addAll(dateColumn, totalSalesColumn);
    }

    private void populateSalesByDate() {
        configureSalesByDateColumns();

        // Fetch completed orders from the database
        List<Order> completedOrders = orderDao.getCompletedOrders();

        // Use a TreeMap to ensure the dates are sorted
        TreeMap<LocalDate, Double> salesByDate = new TreeMap<>();

        for (Order order : completedOrders) {
            LocalDate date = order.getCreatedAt().toLocalDate();
            salesByDate.merge(date, order.getTotalAmount(), Double::sum);
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Sales by Date");
        ObservableList<Object> tableData = FXCollections.observableArrayList();

        for (Map.Entry<LocalDate, Double> entry : salesByDate.entrySet()) {
            String dateAsString = entry.getKey().toString();
            Double totalSales = entry.getValue();
            series.getData().add(new XYChart.Data<>(dateAsString, totalSales));

            DailySalesReport dailySalesReport = new DailySalesReport(entry.getKey(), totalSales);
            tableData.add(dailySalesReport);
        }

        salesBarChart.getData().add(series);
        reportTableView.setItems(tableData);
    }

    private void configureInventoryLevelColumns() {

    }

    private void populateInventoryLevel() {

    }

    private void configurePopularItemsColumns() {
        // Define and add columns specific to the 'Popular Items' report
    }

    private void populatePopularItems() {
    }

    @FXML
    private void onFilterApplied() {
        String selectedReport = reportTypeComboBox.getSelectionModel().getSelectedItem();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        String category = categoryFilter.getText();
    }

    @FXML
    protected void onGenerateReport(ActionEvent event) {
        onFilterApplied();
    }
}
