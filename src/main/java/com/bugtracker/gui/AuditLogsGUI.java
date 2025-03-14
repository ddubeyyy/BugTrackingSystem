package com.bugtracker.gui;

import com.bugtracker.db.DatabaseConnection;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;


public class AuditLogsGUI {
    public AuditLogsGUI() {
        JFrame frame = new JFrame("Audit Logs");
        frame.setSize(900, 600);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JLabel lblTitle = new JLabel("Audit Logs", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setOpaque(true);
        lblTitle.setBackground(new Color(50, 115, 220));
        lblTitle.setPreferredSize(new Dimension(900, 50));
        frame.add(lblTitle, BorderLayout.NORTH);

        String[] columns = {"User ID", "Action", "Timestamp"};
        List<String[]> data = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM audit_log ORDER BY timestamp DESC");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                data.add(new String[]{
                        String.valueOf(rs.getInt("user_id")),
                        rs.getString("action"),
                        rs.getString("timestamp")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 🌟 Convert List to Table Model
        DefaultTableModel model = new DefaultTableModel(data.toArray(new String[0][0]), columns);
        JTable table = new JTable(model);

        // 🌟 Style Table
        table.setRowHeight(30);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(50, 115, 220));
        table.getTableHeader().setForeground(Color.WHITE);

        // 🌟 Center Align Table Data
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);

        // ✅ Close Button
        JPanel panel = new JPanel();
        JButton btnClose = new JButton("Close");
        btnClose.setFont(new Font("Arial", Font.BOLD, 14));
        btnClose.setBackground(new Color(220, 53, 69)); // Red color
        btnClose.setForeground(Color.WHITE);
        btnClose.setFocusPainted(false);
        btnClose.addActionListener(e -> frame.dispose());
        panel.add(btnClose);

        frame.add(panel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }
}