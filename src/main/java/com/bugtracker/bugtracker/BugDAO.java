package com.bugtracker.bugtracker;

import com.bugtracker.auth.SessionManager;
import com.bugtracker.email.EmailSender;
import com.bugtracker.db.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import com.bugtracker.admin.AuditLogDAO;


public class BugDAO {
    public List<Bug> getAllBugs() {
        List<Bug> bugs = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM bugs");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Bug bug = new Bug(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("status"),
                        rs.getString("priority")
                );
                bugs.add(bug);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bugs;
    }
    public void assignBug(int bugId, int developerId, int adminId) {
        try (Connection con = DatabaseConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("UPDATE bugs SET assigned_to = ? WHERE id = ?");
            ps.setInt(1, developerId);
            ps.setInt(2, bugId);
            ps.executeUpdate();
            AuditLogDAO auditLogDAO = new AuditLogDAO();
            auditLogDAO.logActivity(adminId, "Assigned Bug #" + bugId + " to Developer ID: " + developerId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void updateBugStatus(int bugId, String status) {
        try (Connection con = DatabaseConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("UPDATE bugs SET status = ? WHERE id = ?");
            ps.setString(1, status);
            ps.setInt(2, bugId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void assignBugWithNotification(int bugId, int developerId, String developerEmail) {
        int adminId = SessionManager.getLoggedInUserId(); // Get logged-in admin

        if (adminId == 0) { // Ensure admin is logged in
            System.out.println("Error: Admin ID not found. Ensure admin is logged in.");
            return;
        }

        assignBug(bugId, developerId, adminId);  // Assign the bug with admin ID

        // Send Email Notification
        String subject = "New Bug Assigned - Bug #" + bugId;
        String message = "A new bug has been assigned to you. Please check the system for details.";
        EmailSender.sendEmail(developerEmail, subject, message);
    }


    public List<Bug> getAssignedBugs(int developerId) {
        List<Bug> bugs = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM bugs WHERE assigned_to = ?");
            ps.setInt(1, developerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Bug bug = new Bug(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("status"),
                        rs.getString("priority")
                );
                bugs.add(bug);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bugs;
    }
    public void updateBugStatusWithNotification(int bugId, String status, String developerEmail) {
        updateBugStatus(bugId, status);  // Update status in database

        // Send Email Notification
        String subject = "Bug Status Updated - Bug #" + bugId;
        String message = "The status of your assigned bug has been updated to: " + status;
        EmailSender.sendEmail(developerEmail, subject, message);
    }
    public void addBug(String title, String description, String priority) {
        try (Connection con = DatabaseConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO bugs (title, description, priority, status) VALUES (?, ?, ?, 'Open')"
            );
            ps.setString(1, title);
            ps.setString(2, description);
            ps.setString(3, priority);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public List<String[]> getUserPerformance() {
        List<String[]> performanceData = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "SELECT u.id, u.name, u.role, " +
                            "(SELECT COUNT(*) FROM bugs WHERE assigned_to = u.id) AS assigned_bugs, " +
                            "(SELECT COUNT(*) FROM bugs WHERE assigned_to = u.id AND status = 'Resolved') AS resolved_bugs " +
                            "FROM users u"
            );
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                performanceData.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("name"),
                        rs.getString("role"),
                        String.valueOf(rs.getInt("assigned_bugs")),
                        String.valueOf(rs.getInt("resolved_bugs"))
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return performanceData;
    }
    public String getDeveloperEmail(int bugId) {
        String developerEmail = null;
        try (Connection con = DatabaseConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "SELECT u.email FROM users u " +
                            "JOIN bugs b ON u.id = b.assigned_to WHERE b.id = ?"
            );
            ps.setInt(1, bugId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                developerEmail = rs.getString("email");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return developerEmail;  // Returns null if no developer is assigned
    }

}
