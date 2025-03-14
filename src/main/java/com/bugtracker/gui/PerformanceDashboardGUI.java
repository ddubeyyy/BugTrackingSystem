package com.bugtracker.gui;

import com.bugtracker.bugtracker.BugDAO;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PerformanceDashboardGUI {
    public PerformanceDashboardGUI() {
        JFrame frame = new JFrame("Performance Dashboard");
        frame.setSize(800, 500);
        frame.setResizable(false);
        frame.setLayout(new BorderLayout());

        BugDAO bugDAO = new BugDAO();
        List<String[]> performanceData = bugDAO.getUserPerformance();

        String[] columns = {"User ID", "Name", "Role", "Assigned Bugs", "Resolved Bugs"};
        String[][] data = new String[performanceData.size()][5];

        for (int i = 0; i < performanceData.size(); i++) {
            data[i] = performanceData.get(i);
        }

        JTable table = new JTable(data, columns);
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);

        frame.setVisible(true);
    }
}
