module com.restaurantmanagementsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires mysql.connector.java;

    opens com.restaurantmanagementsystem.pos to javafx.fxml;
    opens com.restaurantmanagementsystem.pos.db to javafx.fxml;
    opens com.restaurantmanagementsystem.pos.controller to javafx.fxml;
    opens com.restaurantmanagementsystem.pos.model to javafx.base, javafx.fxml;

    exports com.restaurantmanagementsystem.pos;
    exports com.restaurantmanagementsystem.pos.controller;
}
