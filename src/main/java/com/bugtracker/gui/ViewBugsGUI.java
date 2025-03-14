package com.bugtracker.gui;

import com.bugtracker.bugtracker.Bug;
import com.bugtracker.bugtracker.BugDAO;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ViewBugsGUI {
    public ViewBugsGUI() {
        JFrame frame = new JFrame("View Bugs");
        frame.setSize(800, 500);
        frame.setResizable(false);
        frame.setLayout(new BorderLayout());

        BugDAO bugDAO = new BugDAO();
        List<Bug> bugs = bugDAO.getAllBugs();

        String[] columns = {"ID", "Title", "Description", "Priority", "Status"};
        String[][] data = new String[bugs.size()][5];

        for (int i = 0; i < bugs.size(); i++) {
            Bug bug = bugs.get(i);
            data[i] = new String[]{String.valueOf(bug.getId()), bug.getTitle(), bug.getDescription(), bug.getPriority(), bug.getStatus()};
        }

        JTable table = new JTable(data, columns);
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);

        frame.setVisible(true);
    }
}