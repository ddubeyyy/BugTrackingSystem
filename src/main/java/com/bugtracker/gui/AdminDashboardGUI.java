package com.bugtracker.gui;

import com.bugtracker.admin.AdminDAO;
import com.bugtracker.auth.SessionManager;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminDashboardGUI {
    public AdminDashboardGUI() {
        JFrame frame = new JFrame("Admin Dashboard");
        frame.setSize(1000, 700);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // 🌟 BACKGROUND PANEL WITH GRADIENT
        JPanel bgPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, new Color(230, 240, 255), 0, getHeight(), new Color(200, 220, 255));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        bgPanel.setLayout(new BorderLayout());
        frame.add(bgPanel, BorderLayout.CENTER);

        // 🌟 HEADER PANEL WITH LOGOUT BUTTON
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(33, 150, 243)); // Material Blue
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel lblTitle = new JLabel("Admin Dashboard", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Roboto", Font.BOLD, 28));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle, BorderLayout.CENTER);

        // 🔹 LOGOUT BUTTON
        JButton btnLogout = new JButton("Logout");
        btnLogout.setFont(new Font("Roboto", Font.BOLD, 14));
        btnLogout.setBackground(new Color(244, 67, 54)); // Material Red
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnLogout.addActionListener(e -> {
            SessionManager.logout();  // Clear session
            frame.dispose();          // Close Admin Dashboard
            new LoginGUI();           // Open Login Screen
        });
        headerPanel.add(btnLogout, BorderLayout.EAST);
        bgPanel.add(headerPanel, BorderLayout.NORTH);

        // 📌 Fetch User Data
        AdminDAO adminDAO = new AdminDAO();
        List<String[]> users = adminDAO.getAllUsers();
        String[] columns = {"ID", "Name", "Email", "Role"};
        DefaultTableModel model = new DefaultTableModel(users.toArray(new String[0][0]), columns);

        JTable table = new JTable(model);
        table.setRowHeight(40);
        table.setFont(new Font("Roboto", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Roboto", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(33, 150, 243)); // Material Blue
        table.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        bgPanel.add(scrollPane, BorderLayout.CENTER);

        // 🌟 STYLE TABLE ROWS
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // 📌 CONTROL PANEL
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(2, 4, 15, 15));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        controlPanel.setBackground(new Color(255, 255, 255, 200)); // Semi-transparent white

        JLabel lblUserId = new JLabel("User ID:");
        lblUserId.setFont(new Font("Roboto", Font.PLAIN, 14));
        JTextField txtUserId = new JTextField();
        txtUserId.setFont(new Font("Roboto", Font.PLAIN, 14));

        JLabel lblRole = new JLabel("New Role:");
        lblRole.setFont(new Font("Roboto", Font.PLAIN, 14));
        String[] roles = {"Tester", "Developer", "Project Manager", "Admin"};
        JComboBox<String> cbRole = new JComboBox<>(roles);
        cbRole.setFont(new Font("Roboto", Font.PLAIN, 14));

        JButton btnUpdate = createStyledButton("Update Role");
        JButton btnAddUser = createStyledButton("Add User");
        JButton btnDeleteUser = createStyledButton("Delete User");
        JButton btnAuditLogs = createStyledButton("View Audit Logs");

        // ✅ Update Role Action
        btnUpdate.addActionListener(e -> {
            int userId = Integer.parseInt(txtUserId.getText());
            String newRole = cbRole.getSelectedItem().toString();
            int adminId = SessionManager.getLoggedInUserId(); // Get admin ID
            adminDAO.updateUserRole(adminId, userId, newRole);
            JOptionPane.showMessageDialog(frame, "User role updated!");
            refreshTable(model, adminDAO);
        });

        // ✅ Add User Action
        btnAddUser.addActionListener(e -> {
            JTextField nameField = new JTextField();
            JTextField emailField = new JTextField();
            JTextField passwordField = new JTextField();
            JComboBox<String> roleField = new JComboBox<>(roles);

            Object[] message = {
                    "Name:", nameField,
                    "Email:", emailField,
                    "Password:", passwordField,
                    "Role:", roleField
            };

            int option = JOptionPane.showConfirmDialog(frame, message, "Add User", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                adminDAO.addUser(nameField.getText(), emailField.getText(), passwordField.getText(),
                        roleField.getSelectedItem().toString());
                JOptionPane.showMessageDialog(frame, "User added successfully!");
                refreshTable(model, adminDAO);
            }
        });

        // ✅ Delete User Action
        btnDeleteUser.addActionListener(e -> {
            int userId = Integer.parseInt(txtUserId.getText());
            adminDAO.deleteUser(userId);
            JOptionPane.showMessageDialog(frame, "User deleted!");
            refreshTable(model, adminDAO);
        });

        // ✅ View Audit Logs
        btnAuditLogs.addActionListener(e -> new AuditLogsGUI());

        // 🔹 ADD COMPONENTS TO PANEL
        controlPanel.add(lblUserId);
        controlPanel.add(txtUserId);
        controlPanel.add(lblRole);
        controlPanel.add(cbRole);
        controlPanel.add(btnUpdate);
        controlPanel.add(btnAddUser);
        controlPanel.add(btnDeleteUser);
        controlPanel.add(btnAuditLogs);

        bgPanel.add(controlPanel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    // 🌟 CREATE STYLED BUTTONS
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Roboto", Font.BOLD, 14));
        button.setBackground(new Color(33, 150, 243)); // Material Blue
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(25, 118, 210)); // Darker Blue on hover
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(33, 150, 243)); // Original Blue
            }
        });
        return button;
    }

    // 🔄 Refresh User Table Without Restarting GUI
    private void refreshTable(DefaultTableModel model, AdminDAO adminDAO) {
        model.setRowCount(0); // Clear existing data
        List<String[]> updatedUsers = adminDAO.getAllUsers();
        for (String[] user : updatedUsers) {
            model.addRow(user);
        }
    }

    public static void main(String[] args) {
        new AdminDashboardGUI();
    }
}