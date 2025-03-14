package com.bugtracker.gui;

import com.bugtracker.chat.ChatServer;
import com.bugtracker.auth.SessionManager;
import com.bugtracker.db.DatabaseConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DeveloperDashboardGUI {
    private String developerName;
    private DefaultTableModel model;
    private JTable table;
    private JTextField txtBugID;

    public DeveloperDashboardGUI(String developerName) {
        this.developerName = developerName;

        JFrame frame = new JFrame("Developer Dashboard");
        frame.setSize(900, 600);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // 🌟 Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(33, 150, 243));

        JLabel lblTitle = new JLabel("Developer Dashboard", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // 🚀 Logout Button
        JButton btnLogout = createStyledButton("Logout", new Color(220, 53, 69), Color.WHITE);
        btnLogout.addActionListener(e -> {
            frame.dispose();
            new LoginGUI();
        });

        headerPanel.add(lblTitle, BorderLayout.CENTER);
        headerPanel.add(btnLogout, BorderLayout.EAST);
        frame.add(headerPanel, BorderLayout.NORTH);

        // 🌟 Table Panel
        String[] columns = {"Bug ID", "Title", "Priority", "Status"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        frame.add(scrollPane, BorderLayout.CENTER);
        loadAssignedBugs();

        // 🌟 Footer Panel (Bug Resolution)
        // 🌟 Footer Panel (Bug Resolution)
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        footerPanel.setBackground(new Color(240, 240, 240));

        JLabel lblBugId = new JLabel("Bug ID:");
        lblBugId.setFont(new Font("Arial", Font.BOLD, 14));

        txtBugID = new JTextField(5);
        txtBugID.setFont(new Font("Arial", Font.PLAIN, 14));

        JButton btnResolve = createStyledButton("Mark as Resolved", new Color(33, 150, 243), Color.WHITE);
        btnResolve.addActionListener(e -> markBugAsResolved());


        JButton btnChat = new JButton("Chat");
        btnChat.setFont(new Font("Arial", Font.BOLD, 14));
        btnChat.setBackground(new Color(50, 115, 220));
        btnChat.setForeground(Color.WHITE);
        btnChat.setFocusPainted(false);
        btnChat.addActionListener(e -> new ChatGUI(SessionManager.getLoggedInUsername(), ChatServer.getOnlineUsers()));


        footerPanel.add(lblBugId);
        footerPanel.add(txtBugID);
        footerPanel.add(btnResolve);
        footerPanel.add(btnChat); // ✅ Add Chat Button to Panel

        frame.add(footerPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    // 🔹 Load Bugs Assigned to Developer
    private void loadAssignedBugs() {
        model.setRowCount(0); // Clear previous data

        try (Connection con = DatabaseConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "SELECT id, title, priority, status FROM bugs WHERE assigned_to = ?"
            );
            ps.setString(1, developerName);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("priority"),
                        rs.getString("status")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🔹 Mark Bug as Resolved
    private void markBugAsResolved() {
        String bugID = txtBugID.getText().trim();
        if (bugID.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please enter a Bug ID!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection con = DatabaseConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "UPDATE bugs SET status = 'Resolved' WHERE id = ? AND assigned_to = ?"
            );
            ps.setInt(1, Integer.parseInt(bugID));
            ps.setString(2, developerName);
            int updated = ps.executeUpdate();

            if (updated > 0) {
                JOptionPane.showMessageDialog(null, "Bug marked as resolved!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadAssignedBugs(); // Refresh table
            } else {
                JOptionPane.showMessageDialog(null, "Error: Bug ID not found or not assigned to you!", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🎨 Create Styled Buttons with Hover Effect
    private JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // ✨ Hover Effect
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }
}
