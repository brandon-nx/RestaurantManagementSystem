package com.restaurantmanagementsystem.pos.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegisterDaoImpl implements RegisterDao {

    @Override
    public boolean registerCustomer(String username, String password, String contact, String address) {
        String sql = "INSERT INTO users (user_id, username, password, role, contact, address) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnector.getConnection()) {

            conn.setAutoCommit(false);
            String newUserId = generateNewUserId(conn);

            if (newUserId == null || checkUsernameExists(username)) {
                conn.rollback();
                return false;
            }

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, newUserId);
                pstmt.setString(2, username);
                pstmt.setString(3, password);
                pstmt.setString(4, "customer");
                pstmt.setString(5, contact);
                pstmt.setString(6, address);

                int affectedRows = pstmt.executeUpdate();
                if (affectedRows == 0) {
                    conn.rollback();
                    return false;
                }

                conn.commit();
                return true;

            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Error occurred during customer registration: " + e.getMessage());
                return false;
            }

        } catch (SQLException e) {
            System.err.println("Authentication failed: " + e.getMessage());
            return false;
        }
    }


    @Override
    public boolean checkUsernameExists(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            System.err.println("Error occurred while checking username existence: " + e.getMessage());
            return true;
        }
    }

    private String generateNewUserId(Connection conn) throws SQLException {
        String sql = "SELECT user_id FROM users ORDER BY created_at DESC LIMIT 1";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                String lastId = rs.getString("user_id");
                int idNumber = Integer.parseInt(lastId.substring(2)) + 1;
                return String.format("U-%03d", idNumber);
            } else {
                return "U-001";
            }
        }
    }
}
