package com.restaurantmanagementsystem.pos.db;

import com.restaurantmanagementsystem.pos.model.UserAuthenticationResult;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginDaoImpl implements LoginDao {

    public UserAuthenticationResult authenticate(String username, String password) {
        String sql = "SELECT user_id, username, role FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String userId = rs.getString("user_id");
                    String retrievedUsername = rs.getString("username");
                    String role = rs.getString("role");

                    return new UserAuthenticationResult(true, retrievedUsername, userId, role);
                }
            }
        } catch (SQLException e) {
            System.err.println("Authentication failed: " + e.getMessage());
        }
        return new UserAuthenticationResult(false, null, null, null);
    }
}
