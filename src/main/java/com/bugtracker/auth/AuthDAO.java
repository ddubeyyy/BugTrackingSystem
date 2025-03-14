package com.bugtracker.auth;

import com.bugtracker.db.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AuthDAO {
    public String authenticateUser(String email, String password) {
        try (Connection con = DatabaseConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT role FROM users WHERE email = ? AND password = ?");
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("role"); // Return user role if authentication is successful
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // Authentication failed
    }
    public boolean resetPassword(String email, String newPassword) {
        try (Connection con = DatabaseConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "UPDATE users SET password = ? WHERE email = ?"
            );
            ps.setString(1, newPassword);
            ps.setString(2, email);
            int rowsUpdated = ps.executeUpdate();
            return rowsUpdated > 0; // Return true if password was updated
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false; // If update fails
    }

}
