package com.rental.ui;

import com.rental.config.DatabaseConnection;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnGoToRegister;

    public LoginFrame() {
        // Set modern Nimbus Look and Feel
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {}

        initComponents();
    }

    private void initComponents() {
        setTitle("Vehicle Rental Management System - Authentication");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 420);
        setResizable(false);
        setLocationRelativeTo(null); // Center frame on screen

        // Main Layout Container
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));
        this.setContentPane(mainPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.gridx = 0;

        // Title Header Banner
        JLabel lblHeader = new JLabel("System Login", JLabel.CENTER);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblHeader.setForeground(new Color(15, 23, 42)); 
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 20, 0);
        mainPanel.add(lblHeader, gbc);
        gbc.insets = new Insets(5, 0, 5, 0);

        // Username Input Layer
        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridy = 1;
        mainPanel.add(lblUsername, gbc);

        txtUsername = new JTextField();
        txtUsername.setPreferredSize(new Dimension(0, 35));
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridy = 2;
        mainPanel.add(txtUsername, gbc);

        // Password Input Layer
        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridy = 3;
        mainPanel.add(lblPassword, gbc);

        txtPassword = new JPasswordField();
        txtPassword.setPreferredSize(new Dimension(0, 35));
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridy = 4;
        mainPanel.add(txtPassword, gbc);

        // Login Action Button
        btnLogin = new JButton("Secure Sign In");
        btnLogin.setPreferredSize(new Dimension(0, 40));
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setBackground(new Color(37, 99, 235)); // Professional blue accent
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 5;
        gbc.insets = new Insets(20, 0, 5, 0);
        mainPanel.add(btnLogin, gbc);

        // Navigation Hyperlink to Account Creation
        btnGoToRegister = new JButton("Don't have an account? Sign up here");
        btnGoToRegister.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnGoToRegister.setForeground(new Color(71, 85, 105));
        btnGoToRegister.setContentAreaFilled(false);
        btnGoToRegister.setBorderPainted(false);
        btnGoToRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 6;
        gbc.insets = new Insets(5, 0, 0, 0);
        mainPanel.add(btnGoToRegister, gbc);

        // ==========================================
        // ACTION EVENT CONTROLS
        // ==========================================
        
        // Triggers credentials verification engine
        btnLogin.addActionListener(e -> performLoginVerification());

        // Routes directly to the new customer onboarding panel
        btnGoToRegister.addActionListener(e -> {
            new RegisterFrame().setVisible(true);
            this.dispose();
        });
    }

    private void performLoginVerification() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in both username and password fields.", "Authentication Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String query = "SELECT role FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String role = rs.getString("role");
                    JOptionPane.showMessageDialog(this, "Authentication successful! Welcome back, " + username + ".", "Access Granted", JOptionPane.INFORMATION_MESSAGE);
                    
                    // Route user to the appropriate portal based on DB privilege role mapping
                    if (role.equalsIgnoreCase("Admin")) {
                        new AdminDashboard().setVisible(true);
                    } else {
                        // Corrected to route seamlessly to your existing CustomerPortal file
                        new CustomerPortal().setVisible(true);
                    }
                    this.dispose(); // Close authentication gate smoothly
                    
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid credentials. Please verify your username and password.", "Access Denied", JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Authentication Server Offline: " + e.getMessage(), "Database Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}