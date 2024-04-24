package com.restaurantmanagementsystem.pos.controller;

import com.restaurantmanagementsystem.pos.model.Report;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class ReportControllerTest {

    private ReportController reportController;
    private Stage stage;

    @BeforeAll
    public static void setupJavaFXRuntime() {
        Platform.startup(() -> {});
    }

    @BeforeEach
    public void setUp() throws Exception {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/restaurantmanagementsystem/pos/view/report.fxml"));
                Parent root = loader.load();
                reportController = loader.getController();
                stage = new Stage();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        Thread.sleep(1000);
    }

    @Test
    public void testReportViewsInitialization() {
        assertNotNull(reportController.reportTypeComboBox);
        assertNotNull(reportController.datePicker);
        assertNotNull(reportController.reportTableView);
        assertNotNull(reportController.categoryColumn);
        assertNotNull(reportController.quantityColumn);
        assertNotNull(reportController.salesColumn);
        assertNotNull(reportController.chartContainer);

        assertEquals(4, reportController.reportTableView.getItems().size());
        assertEquals(LocalDate.now(), reportController.datePicker.getValue());
        assertEquals("Daily Sales Report", reportController.reportTypeComboBox.getValue());
    }

    @Test
    public void testDailyReportViews() {
        Platform.runLater(() -> {
            reportController.reportTypeComboBox.getSelectionModel().select("Daily Sales Report");
            reportController.datePicker.setValue(LocalDate.now());
        });

        List<String> expectedCategories = Arrays.asList("Breakfast", "Lunch", "Dinner", "Others");

        List<String> actualCategories = new ArrayList<>();
        for (Report report : reportController.reportTableView.getItems()) {
            actualCategories.add(report.getCategory());
        }

        assertEquals(expectedCategories.size(), actualCategories.size());
        assertTrue(actualCategories.containsAll(expectedCategories));
    }

    @AfterEach
    public void tearDown() throws Exception {
        Platform.runLater(() -> stage.close());
    }
}
