package com.bugtracker.admin;

import com.bugtracker.db.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class AdminDAO {

    // ✅ Get All Users
    public List<String[]> getAllUsers() {
        List<String[]> users = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT id, name, email, role FROM users");
            ResultSet rs = ps.executeQuery();

            // Debugging: Print data to console
            System.out.println("Fetching Users:");

            while (rs.next()) {
                String[] userData = new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("role")
                };
                users.add(userData);

                // Debugging: Print each fetched user
                System.out.println("User: " + userData[0] + " - " + userData[1] + " - " + userData[2] + " - " + userData[3]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }


    // ✅ Add New User
    public void addUser(String name, String email, String password, String role) {
        try (Connection con = DatabaseConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)");
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, password);
            ps.setString(4, role);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ✅ Update User Role
    // ✅ Update User Role (Fixed)
    public void updateUserRole(int adminId, int userId, String newRole) {
        try (Connection con = DatabaseConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("UPDATE users SET role = ? WHERE id = ?");
            ps.setString(1, newRole);
            ps.setInt(2, userId);
            ps.executeUpdate();

            AuditLogDAO auditLogDAO = new AuditLogDAO();
            auditLogDAO.logActivity(adminId, "Updated User ID: " + userId + " to role: " + newRole);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // ✅ Delete User
    public void deleteUser(int userId) {
        try (Connection con = DatabaseConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("DELETE FROM users WHERE id = ?");
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void logActivity(int userId, String action) {
        try (Connection con = DatabaseConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO audit_log (user_id, action) VALUES (?, ?)"
            );
            ps.setInt(1, userId);
            ps.setString(2, action);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
