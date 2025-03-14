package com.bugtracker.gui;

import com.bugtracker.bugtracker.BugDAO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UpdateBugStatusGUI {
    public UpdateBugStatusGUI() {
        JFrame frame = new JFrame("Update Bug Status");
        frame.setSize(800, 500);
        frame.setResizable(false);
        frame.setLayout(new GridLayout(4, 2));

        JLabel lblBugId = new JLabel("Bug ID:");
        JTextField txtBugId = new JTextField();
        JLabel lblStatus = new JLabel("New Status:");
        String[] statuses = {"Open", "In Progress", "Resolved", "Closed"};
        JComboBox<String> cbStatus = new JComboBox<>(statuses);
        JButton btnUpdate = new JButton("Update Status");

        frame.add(lblBugId);
        frame.add(txtBugId);
        frame.add(lblStatus);
        frame.add(cbStatus);
        frame.add(new JLabel());  // Spacer
        frame.add(btnUpdate);

        btnUpdate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int bugId = Integer.parseInt(txtBugId.getText());
                String status = cbStatus.getSelectedItem().toString();

                BugDAO bugDAO = new BugDAO();
                String developerEmail = bugDAO.getDeveloperEmail(bugId); // Fetch assigned developer's email

                if (developerEmail != null) {
                    bugDAO.updateBugStatusWithNotification(bugId, status, developerEmail);
                    JOptionPane.showMessageDialog(frame, "Bug status updated successfully!");
                } else {
                    JOptionPane.showMessageDialog(frame, "No developer assigned to this bug!");
                }

                frame.dispose();
            }
        });


        frame.setVisible(true);
    }
}
