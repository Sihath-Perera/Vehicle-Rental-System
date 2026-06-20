package com.rental.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginFrame extends JFrame {

    // Core variables precisely matching our backend architecture requirements
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbRole;
    private JButton btnLogin;
    private JLabel lblForgotPassword;
    private JLabel lblSignUp;

    public LoginFrame() {
        // Look & Feel synchronization - Forces identical rendering across all laptops
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Fallback safely to system default if Nimbus is missing
        }

        initComponents();
    }

    private void initComponents() {
        // Main window attributes
        setTitle("Vehicle Rental System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(850, 550);
        setResizable(false);
        setLocationRelativeTo(null); // Spawns window dead center of the desktop

        // Main Background Panel (Soft modern light gray/blue canvas)
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(244, 246, 249));
        this.setContentPane(mainPanel);

        // Central White Card Container Panel
        JPanel pnlLoginCard = new JPanel(new GridBagLayout());
        pnlLoginCard.setBackground(Color.WHITE);
        pnlLoginCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 233, 238), 1),
                BorderFactory.createEmptyBorder(30, 45, 30, 45)
        ));

        // Shared Layout Constraint Settings
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0); 
        gbc.gridx = 0;

        // 1. App Main Title Header
        JLabel lblTitle = new JLabel("VEHICLE RENTAL SYSTEM", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(30, 58, 138)); // Rich dark blue corporate tint
        gbc.gridy = 0;
        pnlLoginCard.add(lblTitle, gbc);

        // 2. Sub-text Header
        JLabel lblSubtitle = new JLabel("Sign In to Your Workspace", JLabel.CENTER);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitle.setForeground(Color.GRAY);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 20, 0); // Spacing gap below headers
        pnlLoginCard.add(lblSubtitle, gbc);

        // Reset spacing for structural elements
        gbc.insets = new Insets(4, 0, 4, 0);

        // 3. Username Inputs
        JLabel lblUser = new JLabel("Username:");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 13));
        gbc.gridy = 2;
        pnlLoginCard.add(lblUser, gbc);

        txtUsername = new JTextField();
        txtUsername.setPreferredSize(new Dimension(280, 35));
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 205, 215), 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        gbc.gridy = 3;
        pnlLoginCard.add(txtUsername, gbc);

        // 4. Password Inputs
        JLabel lblPass = new JLabel("Password:");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 13));
        gbc.gridy = 4;
        pnlLoginCard.add(lblPass, gbc);

        txtPassword = new JPasswordField();
        txtPassword.setPreferredSize(new Dimension(280, 35));
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 205, 215), 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        gbc.gridy = 5;
        pnlLoginCard.add(txtPassword, gbc);

        // 5. Role Target Selector
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

        // 6. Action Submission Button (Primary Color Action Accent)
        btnLogin = new JButton("Login");
        btnLogin.setPreferredSize(new Dimension(280, 40));
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setBackground(new Color(37, 99, 235)); // High-visibility royal blue
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 8;
        gbc.insets = new Insets(20, 0, 10, 0); // Pad spacing directly above button execution
        pnlLoginCard.add(btnLogin, gbc);

        // 7. Auxiliary Links (Footer Area Styling)
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

        // Nest the completed central card inside the main background anchor window
        mainPanel.add(pnlLoginCard);
        
        // Add minimal hovering effect behavior logic for hyperlinks to polish presentation feel
        hookHyperlinkStyles();
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

    // Main deployment mechanism enabling instant run capabilities right inside NetBeans
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}