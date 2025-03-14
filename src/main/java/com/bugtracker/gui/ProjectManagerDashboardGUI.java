package com.bugtracker.gui;

import com.bugtracker.db.DatabaseConnection;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.border.EmptyBorder;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ProjectManagerDashboardGUI {
    private JFrame frame;
    private JTextArea textArea; // Declare textArea at class level

    public ProjectManagerDashboardGUI() {
        initializeUI();
    }
    private void initializeUI() {
        frame = new JFrame("Project Manager Dashboard");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Full screen
        frame.setMinimumSize(new Dimension(900, 500));

        // 🔹 Header Panel
        JPanel headerPanel = createHeaderPanel();
        frame.add(headerPanel, BorderLayout.NORTH);

        // 🔹 Tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Roboto", Font.PLAIN, 16));
        tabbedPane.setBackground(new Color(33, 150, 243)); // Material Blue
        tabbedPane.setForeground(Color.WHITE);

        tabbedPane.add("Monitor Bugs", createBugsPanel());
        tabbedPane.add("Monitor Testers", createTestersPanel());
        tabbedPane.add("Monitor Developers", createDevelopersPanel());
        tabbedPane.add("Performance Metrics", createPerformancePanel());

        frame.add(tabbedPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(33, 150, 243)); // Material Blue
        headerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        // Title Label
        JLabel lblTitle = new JLabel("Project Manager Dashboard", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Roboto", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);

        // Logout Button
        JButton btnLogout = new JButton("Logout");
        btnLogout.setFont(new Font("Roboto", Font.BOLD, 14));
        btnLogout.setBackground(new Color(244, 67, 54)); // Material Red
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setBorder(new EmptyBorder(10, 20, 10, 20));
        btnLogout.addActionListener(e -> {
            frame.dispose();
            new LoginGUI(); // Redirect to login
        });

        headerPanel.add(lblTitle, BorderLayout.CENTER);
        headerPanel.add(btnLogout, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createBugsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.WHITE);

        String[] columns = {"Bug ID", "Title", "Priority", "Assigned To", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        table.setFont(new Font("Roboto", Font.PLAIN, 14));
        table.setRowHeight(25);
        table.setShowGrid(false);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.add(scrollPane, BorderLayout.CENTER);
        loadBugs(model);

        return panel;
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
                        rs.getString("assigned_to"),
                        rs.getString("status")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ✅ Monitor Testers Panel
    private JPanel createTestersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.WHITE);

        String[] columns = {"Tester ID", "Name", "Email", "Bugs Reported"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        table.setFont(new Font("Roboto", Font.PLAIN, 14));
        table.setRowHeight(25);
        table.setShowGrid(false);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.add(scrollPane, BorderLayout.CENTER);
        loadTesterPerformance(model);

        return panel;
    }

    private void loadTesterPerformance(DefaultTableModel model) {
        try (Connection con = DatabaseConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "SELECT users.id, users.name, users.email, COUNT(bugs.id) AS total_bugs " +
                            "FROM users LEFT JOIN bugs ON users.id = bugs.reported_by " +
                            "WHERE users.role = 'Tester' GROUP BY users.id"
            );
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getInt("total_bugs")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ✅ Monitor Developers Panel
    private JPanel createDevelopersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.WHITE);

        String[] columns = {"Developer Name", "Bugs Assigned", "Bugs Resolved"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        table.setFont(new Font("Roboto", Font.PLAIN, 14));
        table.setRowHeight(25);
        table.setShowGrid(false);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.add(scrollPane, BorderLayout.CENTER);
        loadDevelopers(model);

        return panel;
    }

    private void loadDevelopers(DefaultTableModel model) {
        try (Connection con = DatabaseConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "SELECT u.name, " +
                            "COUNT(CASE WHEN b.status = 'Open' THEN 1 END) AS bugs_assigned, " +
                            "COUNT(CASE WHEN b.status = 'Resolved' THEN 1 END) AS bugs_resolved " +
                            "FROM users u " +
                            "LEFT JOIN bugs b ON u.name = b.assigned_to " +
                            "WHERE u.role = 'Developer' " +
                            "GROUP BY u.name"
            );
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("name"),
                        rs.getInt("bugs_assigned"),
                        rs.getInt("bugs_resolved")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JPanel createPerformancePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.WHITE);


        JTextArea textArea = new JTextArea();
        textArea.setFont(new Font("Roboto", Font.PLAIN, 14));
        textArea.setEditable(false);
        textArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.add(new JScrollPane(textArea), BorderLayout.WEST);

        loadPerformanceMetrics(textArea);


        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        loadPerformanceMetricsForChart(dataset);


        JFreeChart chart = ChartFactory.createBarChart(
                "Developer Performance Metrics",
                "Developers",
                "Number of Bugs",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(600, 400));

        panel.add(chartPanel, BorderLayout.CENTER);

        return panel;
    }


    private void loadPerformanceMetrics(JTextArea textArea) {
        try (Connection con = DatabaseConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "SELECT u.name, COUNT(b.id) AS total_bugs, " +
                            "COUNT(CASE WHEN b.status = 'Resolved' THEN 1 END) AS resolved_bugs " +
                            "FROM users u " +
                            "LEFT JOIN bugs b ON u.name = b.assigned_to " +
                            "WHERE u.role = 'Developer' " +
                            "GROUP BY u.name"
            );
            ResultSet rs = ps.executeQuery();

            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                sb.append(rs.getString("name")).append(": ")
                        .append(rs.getInt("total_bugs")).append(" total, ")
                        .append(rs.getInt("resolved_bugs")).append(" resolved\n");
            }
            textArea.setText(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void loadPerformanceMetricsForChart(DefaultCategoryDataset dataset) {
        try (Connection con = DatabaseConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "SELECT u.name, " +
                            "COUNT(b.id) AS total_bugs, " +
                            "COUNT(CASE WHEN b.status = 'Resolved' THEN 1 END) AS resolved_bugs " +
                            "FROM users u " +
                            "LEFT JOIN bugs b ON u.name = b.assigned_to " +
                            "WHERE u.role = 'Developer' " +
                            "GROUP BY u.name"
            );
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String devName = rs.getString("name");
                int totalBugs = rs.getInt("total_bugs");
                int resolvedBugs = rs.getInt("resolved_bugs");

                dataset.addValue(totalBugs, "Total Bugs", devName);
                dataset.addValue(resolvedBugs, "Resolved Bugs", devName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showPerformanceChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        loadPerformanceMetricsForChart(dataset);  // ✅ Call the correct method

        JFreeChart chart = ChartFactory.createBarChart(
                "Developer Performance Metrics",
                "Developers",
                "Number of Bugs",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );

        JFrame chartFrame = new JFrame("Performance Metrics");
        chartFrame.setSize(800, 600);
        chartFrame.add(new ChartPanel(chart));
        chartFrame.setVisible(true);
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ProjectManagerDashboardGUI());
    }
}