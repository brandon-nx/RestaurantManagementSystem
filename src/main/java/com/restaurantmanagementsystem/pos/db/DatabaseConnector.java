package com.restaurantmanagementsystem.pos.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {

    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/restaurant_pos";
    private static final String DATABASE_USER = "root";
    private static final String DATABASE_PASSWORD = "Bran0767#";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
    }

    // Check if database is successfully connected

//    public static void main(String[] args) {
//        Connection connection = null;
//        try {
//            connection = DatabaseConnector.getConnection();
//            if (connection != null) {
//                System.out.println("Connection to the database was successful!");
//            }
//        } catch (SQLException e) {
//            System.out.println("An error occurred while connecting to the database.");
//            e.printStackTrace();
//        } finally {
//            // It's important to close the connection when you're done with it
//            try {
//                if (connection != null && !connection.isClosed()) {
//                    connection.close();
//                }
//            } catch (SQLException e) {
//                System.out.println("An error occurred while closing the database connection.");
//                e.printStackTrace();
//            }
//        }
//    }
}
