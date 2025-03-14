package com.bugtracker.admin;

import com.bugtracker.db.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

public class AuditLogDAO {
    public void logActivity(int userId, String action) {
        try (Connection con = DatabaseConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO audit_log (user_id, action, timestamp) VALUES (?, ?, ?)"
            );
            ps.setInt(1, userId);
            ps.setString(2, action);
            ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
