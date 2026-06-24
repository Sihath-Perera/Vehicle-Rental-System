package com.rental.ui;

import com.rental.controller.RentalController;
import com.rental.entities.User;
import com.rental.entities.Customer; // Added the Customer import

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * Customer Authentication System interface.
 * Implements the DriveFlow single-column card design.
 * @author 
 */
public class LoginFrame extends JFrame {
    private RentalController controller;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnGoToRegister;
    
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }

    public LoginFrame() {
        this.controller = new RentalController();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("DriveFlow - Secure Login");
        setSize(450, 550); // Compact vertical layout for login
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        // Background Canvas Layer
        JPanel backgroundPanel = new JPanel(new GridBagLayout());
        backgroundPanel.setBackground(new Color(248, 250, 252)); // Soft Slate

        // Crisp White Login Card
        JPanel cardPanel = new JPanel(new GridBagLayout());
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            new EmptyBorder(40, 40, 40, 40)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        // --- Header Section ---
        JLabel lblTitle = new JLabel("Welcome Back", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(new Color(15, 23, 42));
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 5, 0);
        cardPanel.add(lblTitle, gbc);

        JLabel lblSubtitle = new JLabel("Sign in to access your dashboard.", SwingConstants.CENTER);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitle.setForeground(new Color(100, 116, 139));
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 40, 0);
        cardPanel.add(lblSubtitle, gbc);

        // --- Input Fields ---
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 5, 0);
        cardPanel.add(createFormLabel("System Username"), gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 20, 0);
        txtUsername = new JTextField(); 
        styleInputField(txtUsername);
        cardPanel.add(txtUsername, gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 5, 0);
        cardPanel.add(createFormLabel("Secure Password"), gbc);

        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 30, 0);
        txtPassword = new JPasswordField(); 
        styleInputField(txtPassword);
        cardPanel.add(txtPassword, gbc);

        // --- Action Buttons ---
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 15, 0);
        
        btnLogin = new JButton("Sign In to DriveFlow");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setBackground(new Color(37, 99, 235)); // Electric Blue Accent
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setPreferredSize(new Dimension(0, 42));
        btnLogin.setFocusPainted(false);
        btnLogin.setBorder(BorderFactory.createEmptyBorder());
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnLogin.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btnLogin.setBackground(new Color(29, 78, 216)); }
            public void mouseExited(MouseEvent e) { btnLogin.setBackground(new Color(37, 99, 235)); }
        });
        btnLogin.addActionListener(e -> executeLogin());
        cardPanel.add(btnLogin, gbc);

        // Register Link (The connection to your RegisterFrame)
        gbc.gridy = 7;
        gbc.insets = new Insets(5, 0, 0, 0);
        btnGoToRegister = new JButton("New client? Create an account");
        btnGoToRegister.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnGoToRegister.setForeground(new Color(37, 99, 235));
        btnGoToRegister.setBorderPainted(false);
        btnGoToRegister.setContentAreaFilled(false);
        btnGoToRegister.setFocusPainted(false);
        btnGoToRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnGoToRegister.addActionListener(e -> {
            new RegisterFrame().setVisible(true); // Opens the register window
            this.dispose(); // Closes this login window
        });
        cardPanel.add(btnGoToRegister, gbc);

        // Assemble the layout
        backgroundPanel.add(cardPanel);
        add(backgroundPanel, BorderLayout.CENTER);
    }

    /**
     * Standardizes input field geometry and typography.
     */
    private void styleInputField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setForeground(new Color(51, 65, 85));
        field.setCaretColor(new Color(37, 99, 235));
        field.setPreferredSize(new Dimension(0, 38));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            BorderFactory.createEmptyBorder(0, 12, 0, 12)
        ));
    }

    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(new Color(71, 85, 105));
        return label;
    }

    private void executeLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        
        // 1. Declare it here so it is visible to the whole method
        User loggedInUser = null; 

        try {
            // 2. Assign the value inside the try block
            loggedInUser = controller.login(username, password);
        } catch (Exception ex) {
            ex.printStackTrace();
            return; // Stop here if database crashes
        }

        // 3. Now you can safely use it here because it is in the same scope
        if (loggedInUser != null) {
            if ("ADMIN".equalsIgnoreCase(loggedInUser.getRole())) {
                new AdminDashboard(loggedInUser).setVisible(true);
            } else {
                // FIXED LINE: Cast to Customer explicitly
                new CustomerPortal((Customer) loggedInUser).setVisible(true);
            }
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Login Failed");
        }
    }
}