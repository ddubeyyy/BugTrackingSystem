package com.bugtracker.gui;

import com.bugtracker.auth.AuthDAO; // Use AuthDAO instead of UserDAO
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ResetPasswordGUI {
    public ResetPasswordGUI() {
        JFrame frame = new JFrame("Reset Password");
        frame.setSize(400, 250);
        frame.setLayout(null);

        JLabel lblEmail = new JLabel("Enter your email:");
        lblEmail.setBounds(50, 50, 150, 30);
        frame.add(lblEmail);

        JTextField txtEmail = new JTextField();
        txtEmail.setBounds(200, 50, 150, 30);
        frame.add(txtEmail);

        JLabel lblNewPassword = new JLabel("New Password:");
        lblNewPassword.setBounds(50, 100, 150, 30);
        frame.add(lblNewPassword);

        JPasswordField txtNewPassword = new JPasswordField();
        txtNewPassword.setBounds(200, 100, 150, 30);
        frame.add(txtNewPassword);

        JButton btnReset = new JButton("Reset Password");
        btnReset.setBounds(100, 150, 200, 30);
        frame.add(btnReset);

        btnReset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String email = txtEmail.getText();
                String newPassword = new String(txtNewPassword.getPassword());
                AuthDAO authDAO = new AuthDAO(); // Use AuthDAO

                boolean success = authDAO.resetPassword(email, newPassword);

                if (success) {
                    JOptionPane.showMessageDialog(frame, "Password reset successful!");
                    frame.dispose();
                } else {
                    JOptionPane.showMessageDialog(frame, "Email not found!");
                }
            }
        });

        frame.setVisible(true);
    }
}
