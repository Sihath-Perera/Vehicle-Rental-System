package com.rental.ui;

import com.rental.config.DatabaseConnection;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegisterFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JTextField txtFullName;
    private JTextField txtAddress;
    private JTextField txtLicense;
    private JButton btnRegister;
    private JButton btnBackToLogin;

    public RegisterFrame() {
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
        setTitle("Customer Account Onboarding");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 550);
        setResizable(false);
        setLocationRelativeTo(null); // Centers the frame on screen

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        this.setContentPane(mainPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.gridx = 0;

        // Header Banner
        JLabel lblTitle = new JLabel("Create New Account", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(15, 23, 42));
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 15, 0);
        mainPanel.add(lblTitle, gbc);
        gbc.insets = new Insets(4, 0, 4, 0);

        // Fields Setup
        JLabel lblUsername = new JLabel("Desired Username:");
        lblUsername.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridy = 1; mainPanel.add(lblUsername, gbc);

        txtUsername = new JTextField();
        txtUsername.setPreferredSize(new Dimension(0, 32));
        gbc.gridy = 2; mainPanel.add(txtUsername, gbc);

        JLabel lblPassword = new JLabel("Account Password:");
        lblPassword.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridy = 3; mainPanel.add(lblPassword, gbc);

        txtPassword = new JPasswordField();
        txtPassword.setPreferredSize(new Dimension(0, 32));
        gbc.gridy = 4; mainPanel.add(txtPassword, gbc);

        JLabel lblName = new JLabel("Full Name:");
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridy = 5; mainPanel.add(lblName, gbc);

        txtFullName = new JTextField();
        txtFullName.setPreferredSize(new Dimension(0, 32));
        gbc.gridy = 6; mainPanel.add(txtFullName, gbc);

        JLabel lblAddress = new JLabel("Residential Address:");
        lblAddress.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridy = 7; mainPanel.add(lblAddress, gbc);

        txtAddress = new JTextField();
        txtAddress.setPreferredSize(new Dimension(0, 32));
        gbc.gridy = 8; mainPanel.add(txtAddress, gbc);

        JLabel lblLicense = new JLabel("Driving License Number:");
        lblLicense.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridy = 9; mainPanel.add(lblLicense, gbc);

        txtLicense = new JTextField();
        txtLicense.setPreferredSize(new Dimension(0, 32));
        gbc.gridy = 10; mainPanel.add(txtLicense, gbc);

        // Buttons
        btnRegister = new JButton("Register Account");
        btnRegister.setPreferredSize(new Dimension(0, 38));
        btnRegister.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnRegister.setBackground(new Color(37, 99, 235));
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setFocusPainted(false);
        btnRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 11;
        gbc.insets = new Insets(15, 0, 5, 0);
        mainPanel.add(btnRegister, gbc);

        btnBackToLogin = new JButton("Back to Login");
        btnBackToLogin.setPreferredSize(new Dimension(0, 32));
        btnBackToLogin.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnBackToLogin.setContentAreaFilled(false);
        btnBackToLogin.setBorderPainted(false);
        btnBackToLogin.setForeground(new Color(100, 116, 139));
        btnBackToLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 12;
        gbc.insets = new Insets(0, 0, 0, 0);
        mainPanel.add(btnBackToLogin, gbc);

        // --- Action Listeners ---
        btnRegister.addActionListener(e -> processRegistration());
        
        btnBackToLogin.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            this.dispose();
        });
    }

    private void processRegistration() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String fullName = txtFullName.getText().trim();
        String address = txtAddress.getText().trim();
        String license = txtLicense.getText().trim();

        // 1. Validation Check
        if (username.isEmpty() || password.isEmpty() || fullName.isEmpty() || address.isEmpty() || license.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All onboarding registration fields must be completed.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Database Processing
        String checkQuery = "SELECT username FROM users WHERE username = ?";
        String insertQuery = "INSERT INTO users (username, password, role, full_name, address, license_number) VALUES (?, ?, 'Customer', ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            
            // Guardrail: Check if username is already taken
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setString(1, username);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        JOptionPane.showMessageDialog(this, "The username '" + username + "' is already taken. Please pick another.", "Account Conflict", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }

            // Execute Account Creation
            try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                insertStmt.setString(1, username);
                insertStmt.setString(2, password);
                insertStmt.setString(3, fullName);
                insertStmt.setString(4, address);
                insertStmt.setString(5, license);

                insertStmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Account provisioned successfully!\nYou can now log in using your credentials.", "Success", JOptionPane.INFORMATION_MESSAGE);
                
                new LoginFrame().setVisible(true);
                this.dispose();
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Onboarding Failed: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}