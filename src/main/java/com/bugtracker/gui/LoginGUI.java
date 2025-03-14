package com.bugtracker.gui;

import com.bugtracker.auth.SessionManager;
import com.bugtracker.db.DatabaseConnection;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Objects;

public class LoginGUI {
    public LoginGUI() {
        JFrame frame = new JFrame("Bug Tracking System - Login");
        frame.setSize(900, 600);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        // 🌟 Background Panel with Gradient
        JPanel bgPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, new Color(33, 150, 243), 0, getHeight(), new Color(25, 118, 210));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        bgPanel.setBounds(0, 0, 900, 600);
        bgPanel.setLayout(null);
        frame.add(bgPanel);

        // 🌟 Title Label
        JLabel lblTitle = new JLabel("BUG TRACKING SYSTEM", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Roboto", Font.BOLD, 40));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBounds(0, 50, 900, 40);
        bgPanel.add(lblTitle);

        ImageIcon emailIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/email_icon.png")));
        Image emailImage = emailIcon.getImage().getScaledInstance(40, 30, Image.SCALE_SMOOTH); // Scale to 30x30
        JLabel lblEmailIcon = new JLabel(new ImageIcon(emailImage));
        lblEmailIcon.setBounds(300, 150, 45, 40);
        bgPanel.add(lblEmailIcon);


        JTextField txtEmail = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        txtEmail.setBounds(340, 150, 260, 40);
        txtEmail.setFont(new Font("Roboto", Font.PLAIN, 14));
        txtEmail.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        txtEmail.setBackground(new Color(255, 255, 255, 200));
        txtEmail.setForeground(Color.BLACK);
        txtEmail.setOpaque(false);
        bgPanel.add(txtEmail);

        ImageIcon passwordIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/password_icon.png")));
        Image passwordImage = passwordIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH); // Scale to 30x30
        JLabel lblPasswordIcon = new JLabel(new ImageIcon(passwordImage));
        lblPasswordIcon.setBounds(300, 210, 40, 30);
        bgPanel.add(lblPasswordIcon);

        // 🌟 Password Input (Rounded and Transparent)
        JPasswordField txtPassword = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        txtPassword.setBounds(340, 210, 260, 40);
        txtPassword.setFont(new Font("Roboto", Font.PLAIN, 14));
        txtPassword.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        txtPassword.setBackground(new Color(255, 255, 255, 200));
        txtPassword.setForeground(Color.BLACK);
        txtPassword.setOpaque(false);
        bgPanel.add(txtPassword);

        // 🌟 Login Button (Rounded and Hover Effect)
        JButton btnLogin = new JButton("Login") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        btnLogin.setBounds(340, 280, 260, 45);
        btnLogin.setFont(new Font("Roboto", Font.BOLD, 16));
        btnLogin.setBackground(new Color(255, 255, 255, 200));
        btnLogin.setForeground(new Color(33, 150, 243));
        btnLogin.setFocusPainted(false);
        btnLogin.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        btnLogin.setOpaque(false);
        btnLogin.setContentAreaFilled(false);
        btnLogin.setBorderPainted(false);
        btnLogin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnLogin.setBackground(new Color(255, 255, 255, 255));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnLogin.setBackground(new Color(255, 255, 255, 200));
            }
        });
        bgPanel.add(btnLogin);

        // 🌟 Forgot Password Button (Sheer Text with Hover Effect)
        JButton btnForgotPassword = new JButton("Forgot Password?");
        btnForgotPassword.setBounds(340, 340, 260, 30);
        btnForgotPassword.setFont(new Font("Roboto", Font.PLAIN, 12));
        btnForgotPassword.setForeground(Color.WHITE);
        btnForgotPassword.setBorderPainted(false);
        btnForgotPassword.setContentAreaFilled(false);
        btnForgotPassword.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnForgotPassword.setForeground(new Color(200, 200, 200));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnForgotPassword.setForeground(Color.WHITE);
            }
        });
        bgPanel.add(btnForgotPassword);

        // ✅ Login Button Action
        btnLogin.addActionListener(e -> {
            String email = txtEmail.getText();
            String password = new String(txtPassword.getPassword());

            try (Connection con = DatabaseConnection.getConnection()) {
                PreparedStatement ps = con.prepareStatement(
                        "SELECT id, name, role FROM users WHERE email = ? AND password = ?"
                );
                ps.setString(1, email);
                ps.setString(2, password);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    int userID = rs.getInt("id");  // ✅ Get User ID
                    String role = rs.getString("role");
                    String developerName = rs.getString("name");  // ✅ Store Developer Name

                    JOptionPane.showMessageDialog(frame, "Login Successful!");

                    frame.dispose();  // Close login window

                    switch (role) {
                        case "Admin":
                            new AdminDashboardGUI();
                            break;
                        case "Project Manager":
                            new ProjectManagerDashboardGUI();
                            break;
                        case "Developer":
                            new DeveloperDashboardGUI(developerName);  // ✅ Pass Developer Name
                            break;
                        case "Tester":
                            SessionManager.setLoggedInUser(userID, developerName);  // ✅ Store logged-in tester’s ID
                            new TesterDashboardGUI();
                            break;
                        default:
                            JOptionPane.showMessageDialog(frame, "Invalid Role!");
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Invalid email or password.");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });


        // ✅ Forgot Password Action
        btnForgotPassword.addActionListener(e -> new ResetPasswordGUI());

        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new LoginGUI();
    }
}