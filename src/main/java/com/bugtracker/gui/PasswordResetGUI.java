package com.bugtracker.gui;

import com.bugtracker.auth.AuthDAO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PasswordResetGUI {
    public PasswordResetGUI() {
        JFrame frame = new JFrame("Password Reset");
        frame.setSize(800, 500);
        frame.setResizable(false);
        frame.setLayout(new GridLayout(3, 2));

        JLabel lblEmail = new JLabel("Email:");
        JTextField txtEmail = new JTextField();
        JLabel lblNewPassword = new JLabel("New Password:");
        JPasswordField txtNewPassword = new JPasswordField();
        JButton btnReset = new JButton("Reset Password");

        frame.add(lblEmail);
        frame.add(txtEmail);
        frame.add(lblNewPassword);
        frame.add(txtNewPassword);
        frame.add(new JLabel());
        frame.add(btnReset);

        btnReset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String email = txtEmail.getText();
                String newPassword = new String(txtNewPassword.getPassword());

                AuthDAO authDAO = new AuthDAO();
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
