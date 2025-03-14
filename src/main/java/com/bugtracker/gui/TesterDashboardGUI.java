package com.bugtracker.gui;

import com.bugtracker.chat.ChatServer;
import com.bugtracker.db.DatabaseConnection;
import com.bugtracker.auth.SessionManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class TesterDashboardGUI {
    public TesterDashboardGUI() {
        JFrame frame = new JFrame("Tester Dashboard");
        frame.setSize(900, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel lblTitle = new JLabel("Tester Dashboard", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setOpaque(true);
        lblTitle.setBackground(new Color(33, 150, 243));
        lblTitle.setPreferredSize(new Dimension(900, 50));

        JButton btnLogout = createStyledButton("Logout", Color.RED);
        btnLogout.addActionListener(e -> {
            frame.dispose();
            new LoginGUI();
        });

        headerPanel.add(lblTitle, BorderLayout.CENTER);
        headerPanel.add(btnLogout, BorderLayout.EAST);
        frame.add(headerPanel, BorderLayout.NORTH);

        // Table for Bugs
        String[] columns = {"Bug ID", "Title", "Priority", "Assigned To", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        table.setRowHeight(25);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);
        loadBugs(model);

        // Action Panel
        JPanel panel = new JPanel();
        JLabel lblBugID = new JLabel("Bug ID:");
        JTextField txtBugID = new JTextField(5);
        JComboBox<String> cbDevelopers = new JComboBox<>(getDevelopers());
        JButton btnAssign = createStyledButton("Assign", new Color(50, 115, 220));
        JButton btnReportBug = createStyledButton("Report Bug", new Color(50, 115, 220));

        JButton btnChat = new JButton("Chat");
        btnChat.setFont(new Font("Arial", Font.BOLD, 14));
        btnChat.setBackground(new Color(50, 115, 220));
        btnChat.setForeground(Color.WHITE);
        btnChat.setFocusPainted(false);
        btnChat.addActionListener(e -> new ChatGUI(SessionManager.getLoggedInUsername(), ChatServer.getOnlineUsers()));
        panel.add(btnChat); // ✅ Add the Chat Button to Tester Dashboard

        panel.add(lblBugID);
        panel.add(txtBugID);
        panel.add(cbDevelopers);
        panel.add(btnAssign);
        panel.add(btnReportBug);
        frame.add(panel, BorderLayout.SOUTH);

        btnAssign.addActionListener(e -> {
            int bugID = Integer.parseInt(txtBugID.getText());
            String developer = cbDevelopers.getSelectedItem().toString();
            assignBugToDeveloper(bugID, developer);
            JOptionPane.showMessageDialog(frame, "Bug assigned successfully!");
            frame.dispose();
            new TesterDashboardGUI();
        });

        btnReportBug.addActionListener(e -> reportBug(frame));
        frame.setVisible(true);
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { button.setBackground(color.darker()); }
            public void mouseExited(MouseEvent e) { button.setBackground(color); }
        });
        return button;
    }

    private String[] getDevelopers() {
        List<String> devs = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT name FROM users WHERE role = 'Developer'");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                devs.add(rs.getString("name"));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return devs.toArray(new String[0]);
    }

    private void loadBugs(DefaultTableModel model) {
        try (Connection con = DatabaseConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "SELECT id, title, priority, assigned_to, status FROM bugs"
            );
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("priority"),
                        rs.getString("assigned_to") == null ? "Not Assigned" : rs.getString("assigned_to"),
                        rs.getString("status")
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void assignBugToDeveloper(int bugID, String developerName) {
        try (Connection con = DatabaseConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "UPDATE bugs SET assigned_to = ? WHERE id = ?"
            );
            ps.setString(1, developerName);
            ps.setInt(2, bugID);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void reportBug(JFrame parentFrame) {
        JDialog dialog = new JDialog(parentFrame, "Report a Bug", true);
        dialog.setSize(500, 300);
        dialog.setLayout(new GridLayout(5, 2));

        JLabel lblTitle = new JLabel("Bug Title:");
        JTextField txtTitle = new JTextField();
        JLabel lblDesc = new JLabel("Description:");
        JTextArea txtDesc = new JTextArea();
        JLabel lblPriority = new JLabel("Priority:");
        JComboBox<String> cbPriority = new JComboBox<>(new String[]{"Low", "Medium", "High"});
        JComboBox<String> cbDeveloper = new JComboBox<>(getDevelopers());
        JButton btnSubmit = createStyledButton("Submit", new Color(50, 115, 220));

        dialog.add(lblTitle); dialog.add(txtTitle);
        dialog.add(lblDesc); dialog.add(txtDesc);
        dialog.add(lblPriority); dialog.add(cbPriority);
        dialog.add(cbDeveloper); dialog.add(btnSubmit);

        btnSubmit.addActionListener(e -> {
            int testerID = SessionManager.getLoggedInUserId();
            if (testerID == 0) {
                JOptionPane.showMessageDialog(dialog, "Error: Tester not logged in!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try (Connection con = DatabaseConnection.getConnection()) {
                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO bugs (title, description, priority, assigned_to, reported_by, status) VALUES (?, ?, ?, ?, ?, 'Open')"
                );
                ps.setString(1, txtTitle.getText());
                ps.setString(2, txtDesc.getText());
                ps.setString(3, cbPriority.getSelectedItem().toString());
                ps.setString(4, cbDeveloper.getSelectedItem().toString());
                ps.setInt(5, testerID);
                ps.executeUpdate();
                dialog.dispose();
                parentFrame.dispose();
                new TesterDashboardGUI();
            } catch (Exception ex) { ex.printStackTrace(); }
        });
        dialog.setVisible(true);
    }
}
