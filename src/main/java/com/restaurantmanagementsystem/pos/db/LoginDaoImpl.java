package com.restaurantmanagementsystem.pos.db;

import com.restaurantmanagementsystem.pos.model.UserAuthenticationResult;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginDaoImpl implements LoginDao {

    public UserAuthenticationResult authenticate(String username, String password) {
        String loginQuery = "SELECT user_id, role FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(loginQuery)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String userId = rs.getString("user_id"); // Use getString instead of getInt
                    String role = rs.getString("role");
                    return new UserAuthenticationResult(true, role, userId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new UserAuthenticationResult(false, null, null);
    }

}
