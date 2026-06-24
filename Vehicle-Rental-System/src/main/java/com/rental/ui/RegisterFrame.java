package com.rental.ui;

import com.rental.controller.RentalController;

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
 * Customer Account Registration System interface.
 * Implements the DriveFlow dual-column card design for optimal screen real estate.
 * * @author Pathum Srinath
 */
public class RegisterFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmPassword;
    private JTextField txtFullName;
    private JTextField txtAddress;
    private JTextField txtLicense;
    private JTextField txtPhone;
    private JButton btnRegister;
    private JButton btnBackToLogin;
    private RentalController controller;

    public RegisterFrame() {
        this.controller = new RentalController();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("DriveFlow - Customer Registration");
        setSize(700, 600); // Expanded width to accommodate the dual-column grid
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        // Background Canvas Layer (Soft Slate)
        JPanel backgroundPanel = new JPanel(new GridBagLayout());
        backgroundPanel.setBackground(new Color(248, 250, 252));

        // Crisp White Registration Card
        JPanel cardPanel = new JPanel(new GridBagLayout());
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            new EmptyBorder(30, 40, 30, 40)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.5;

        // --- Header Section ---
        JLabel lblTitle = new JLabel("Create Account", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(new Color(15, 23, 42));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 5, 0);
        cardPanel.add(lblTitle, gbc);

        JLabel lblSubtitle = new JLabel("Join the DriveFlow enterprise fleet network.", SwingConstants.CENTER);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitle.setForeground(new Color(100, 116, 139));
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 30, 0);
        cardPanel.add(lblSubtitle, gbc);

        // --- Form Section (Dual Column Layout) ---
        gbc.gridwidth = 1;

        // Row 2: Full Name & Phone Number
        gbc.gridy = 2; gbc.gridx = 0; gbc.insets = new Insets(0, 0, 5, 15);
        cardPanel.add(createFormLabel("Full Name"), gbc);
        
        gbc.gridx = 1; gbc.insets = new Insets(0, 15, 5, 0);
        cardPanel.add(createFormLabel("Phone Number"), gbc);

        gbc.gridy = 3; gbc.gridx = 0; gbc.insets = new Insets(0, 0, 15, 15);
        txtFullName = new JTextField(); styleInputField(txtFullName);
        cardPanel.add(txtFullName, gbc);

        gbc.gridx = 1; gbc.insets = new Insets(0, 15, 15, 0);
        txtPhone = new JTextField(); styleInputField(txtPhone);
        cardPanel.add(txtPhone, gbc);

        // Row 3: Physical Address & License Number
        gbc.gridy = 4; gbc.gridx = 0; gbc.insets = new Insets(0, 0, 5, 15);
        cardPanel.add(createFormLabel("Physical Address"), gbc);

        gbc.gridx = 1; gbc.insets = new Insets(0, 15, 5, 0);
        cardPanel.add(createFormLabel("License Number"), gbc);

        gbc.gridy = 5; gbc.gridx = 0; gbc.insets = new Insets(0, 0, 15, 15);
        txtAddress = new JTextField(); styleInputField(txtAddress);
        cardPanel.add(txtAddress, gbc);

        gbc.gridx = 1; gbc.insets = new Insets(0, 15, 15, 0);
        txtLicense = new JTextField(); styleInputField(txtLicense);
        cardPanel.add(txtLicense, gbc);

        // Row 4: Username (Spans both columns)
        gbc.gridy = 6; gbc.gridx = 0; gbc.gridwidth = 2; gbc.insets = new Insets(0, 0, 5, 0);
        cardPanel.add(createFormLabel("System Username"), gbc);

        gbc.gridy = 7; gbc.insets = new Insets(0, 0, 15, 0);
        txtUsername = new JTextField(); styleInputField(txtUsername);
        cardPanel.add(txtUsername, gbc);

        // Row 5: Passwords
        gbc.gridwidth = 1;
        gbc.gridy = 8; gbc.gridx = 0; gbc.insets = new Insets(0, 0, 5, 15);
        cardPanel.add(createFormLabel("Secure Password"), gbc);

        gbc.gridx = 1; gbc.insets = new Insets(0, 15, 5, 0);
        cardPanel.add(createFormLabel("Confirm Password"), gbc);

        gbc.gridy = 9; gbc.gridx = 0; gbc.insets = new Insets(0, 0, 25, 15);
        txtPassword = new JPasswordField(); styleInputField(txtPassword);
        cardPanel.add(txtPassword, gbc);

        gbc.gridx = 1; gbc.insets = new Insets(0, 15, 25, 0);
        txtConfirmPassword = new JPasswordField(); styleInputField(txtConfirmPassword);
        cardPanel.add(txtConfirmPassword, gbc);

        // --- Action Buttons ---
        gbc.gridy = 10; gbc.gridx = 0; gbc.gridwidth = 2; gbc.insets = new Insets(10, 0, 10, 0);
        
        btnRegister = new JButton("Register Client Account");
        btnRegister.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRegister.setBackground(new Color(37, 99, 235)); // Electric Blue Accent
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setPreferredSize(new Dimension(0, 42));
        btnRegister.setFocusPainted(false);
        btnRegister.setBorder(BorderFactory.createEmptyBorder());
        btnRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnRegister.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btnRegister.setBackground(new Color(29, 78, 216)); }
            public void mouseExited(MouseEvent e) { btnRegister.setBackground(new Color(37, 99, 235)); }
        });
        btnRegister.addActionListener(e -> executeRegistration());
        cardPanel.add(btnRegister, gbc);

        gbc.gridy = 11; gbc.insets = new Insets(5, 0, 0, 0);
        btnBackToLogin = new JButton("Already registered? Return to Sign In");
        btnBackToLogin.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnBackToLogin.setForeground(new Color(37, 99, 235));
        btnBackToLogin.setBorderPainted(false);
        btnBackToLogin.setContentAreaFilled(false);
        btnBackToLogin.setFocusPainted(false);
        btnBackToLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBackToLogin.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            this.dispose();
        });
        cardPanel.add(btnBackToLogin, gbc);

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

    private void executeRegistration() {
        String fullName = txtFullName.getText().trim();
        String address = txtAddress.getText().trim();
        String license = txtLicense.getText().trim();
        String phone = txtPhone.getText().trim();
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        String confirmPassword = new String(txtConfirmPassword.getPassword());

        if (fullName.isEmpty() || address.isEmpty() || license.isEmpty() || phone.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All input data fields are strictly mandatory.", "Validation Failure", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match. Re-enter credentials accurately.", "Security Mismatch", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            boolean success = controller.registerCustomer(username, password, fullName, address, license, phone);

            if (success) {
                JOptionPane.showMessageDialog(this, "Account registered successfully! Redirecting to login terminal.", "System Notice", JOptionPane.INFORMATION_MESSAGE);
                new LoginFrame().setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Username collision or system error. Registration rejected.", "Data Collision", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database Infrastructure Error: " + ex.getMessage(), "Fatal SQL Pipeline Exception", JOptionPane.ERROR_MESSAGE);
        }
    }
}