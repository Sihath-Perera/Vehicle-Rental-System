package com.rental.ui;

import com.rental.config.DatabaseConnection; // Imports your working connection!
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbRole;
    private JButton btnLogin;
    private JLabel lblForgotPassword;
    private JLabel lblSignUp;

    public LoginFrame() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Fallback safely to default
        }
        initComponents();
    }

    private void initComponents() {
        setTitle("Vehicle Rental System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(850, 550);
        setResizable(false);
        setLocationRelativeTo(null); 

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(244, 246, 249));
        this.setContentPane(mainPanel);

        JPanel pnlLoginCard = new JPanel(new GridBagLayout());
        pnlLoginCard.setBackground(Color.WHITE);
        pnlLoginCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 233, 238), 1),
                BorderFactory.createEmptyBorder(30, 45, 30, 45)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0); 
        gbc.gridx = 0;

        // Title
        JLabel lblTitle = new JLabel("VEHICLE RENTAL SYSTEM", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(30, 58, 138)); 
        gbc.gridy = 0;
        pnlLoginCard.add(lblTitle, gbc);

        // Subtitle
        JLabel lblSubtitle = new JLabel("Sign In to Your Workspace", JLabel.CENTER);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitle.setForeground(Color.GRAY);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 20, 0); 
        pnlLoginCard.add(lblSubtitle, gbc);

        gbc.insets = new Insets(4, 0, 4, 0);

        // Username
        JLabel lblUser = new JLabel("Username:");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 13));
        gbc.gridy = 2;
        pnlLoginCard.add(lblUser, gbc);

        txtUsername = new JTextField();
        txtUsername.setPreferredSize(new Dimension(280, 35));
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridy = 3;
        pnlLoginCard.add(txtUsername, gbc);

        // Password
        JLabel lblPass = new JLabel("Password:");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 13));
        gbc.gridy = 4;
        pnlLoginCard.add(lblPass, gbc);

        txtPassword = new JPasswordField();
        txtPassword.setPreferredSize(new Dimension(280, 35));
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridy = 5;
        pnlLoginCard.add(txtPassword, gbc);

        // Role Selector
        JLabel lblRole = new JLabel("Login As:");
        lblRole.setFont(new Font("Segoe UI", Font.BOLD, 13));
        gbc.gridy = 6;
        pnlLoginCard.add(lblRole, gbc);

        cmbRole = new JComboBox<>(new String[]{"CUSTOMER", "ADMIN"});
        cmbRole.setPreferredSize(new Dimension(280, 35));
        cmbRole.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbRole.setBackground(Color.WHITE);
        gbc.gridy = 7;
        pnlLoginCard.add(cmbRole, gbc);

        // Login Button
        btnLogin = new JButton("Login");
        btnLogin.setPreferredSize(new Dimension(280, 40));
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setBackground(new Color(37, 99, 235)); 
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 8;
        gbc.insets = new Insets(20, 0, 10, 0); 
        pnlLoginCard.add(btnLogin, gbc);

        // Footer Links
        lblForgotPassword = new JLabel("Forgot Password?", JLabel.CENTER);
        lblForgotPassword.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblForgotPassword.setForeground(new Color(37, 99, 235));
        lblForgotPassword.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 9;
        gbc.insets = new Insets(5, 0, 5, 0);
        pnlLoginCard.add(lblForgotPassword, gbc);

        lblSignUp = new JLabel("Don't have an account? Sign Up", JLabel.CENTER);
        lblSignUp.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSignUp.setForeground(Color.DARK_GRAY);
        lblSignUp.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 10;
        pnlLoginCard.add(lblSignUp, gbc);

        mainPanel.add(pnlLoginCard);
        
        hookHyperlinkStyles();

        // ⚡ BACKEND ACTION BINDING
        btnLogin.addActionListener(e -> handleLoginExecution());
    }

    /**
     * Executes cloud SQL query validation to authorize users securely
     */
    private void handleLoginExecution() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String selectedRole = cmbRole.getSelectedItem().toString();

        // 1. Input Validation Check
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                    "Please fill in both Username and Password fields.", 
                    "Input Error", 
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Querying Cloud Database via Secure PreparedStatements
        String query = "SELECT * FROM users WHERE username = ? AND password = ? AND role = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, selectedRole);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Match found! Route user based on their system role
                    JOptionPane.showMessageDialog(this, "Login Successful! Welcome, " + username + ".");
                    
                    if (selectedRole.equals("ADMIN")) {
                        new AdminDashboard().setVisible(true); // Open Admin panel
                    } else {
                        new CustomerPortal().setVisible(true); // Open Customer portal
                    }
                    
                    this.dispose(); // Close the Login screen cleanly
                } else {
                    // Incorrect details entered
                    JOptionPane.showMessageDialog(this, 
                            "Invalid Username, Password, or Role selection.", 
                            "Login Failed", 
                            JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                    "Database Error: " + ex.getMessage(), 
                    "System Error", 
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void hookHyperlinkStyles() {
        lblForgotPassword.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { lblForgotPassword.setText("<html><u>Forgot Password?</u></html>"); }
            public void mouseExited(MouseEvent e) { lblForgotPassword.setText("Forgot Password?"); }
        });
        lblSignUp.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { lblSignUp.setText("<html>Don't have an account? <u>Sign Up</u></html>"); }
            public void mouseExited(MouseEvent e) { lblSignUp.setText("Don't have an account? Sign Up"); }
        });
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}