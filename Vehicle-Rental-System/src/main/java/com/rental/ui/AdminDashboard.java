package com.rental.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AdminDashboard extends JFrame {

    // Core variables precisely matching our backend management requirements
    private JTable tblVehicles;
    private DefaultTableModel tableModel;
    private JTextField txtPlateNumber;
    private JTextField txtBrand;
    private JTextField txtModel;
    private JTextField txtDailyRate;
    private JComboBox<String> cmbStatus;
    private JButton btnAddVehicle;
    private JButton btnUpdateStatus;
    private JButton btnLogout;

    public AdminDashboard() {
        // Look & Feel synchronization - Keeps styling identical across all team laptops
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Fallback safely to system default
        }

        initComponents();
    }

    private void initComponents() {
        // Main window attributes
        setTitle("Admin Dashboard - Fleet Management Control");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 650);
        setResizable(false);
        setLocationRelativeTo(null); // Centers window on screen

        // Main Layout Canvas
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(new Color(244, 246, 249));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        this.setContentPane(mainPanel);

        // ==========================================
        // 1. TOP CONTROL HEADER BANNER
        // ==========================================
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(15, 23, 42)); // Slate dark charcoal color for Admin context
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblAdminTitle = new JLabel("Administrative Control Panel", JLabel.LEFT);
        lblAdminTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblAdminTitle.setForeground(Color.WHITE);
        topPanel.add(lblAdminTitle, BorderLayout.WEST);

        btnLogout = new JButton("Logout");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.setBackground(new Color(220, 38, 38)); // Alert Red
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        topPanel.add(btnLogout, BorderLayout.EAST);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // ==========================================
        // 2. CENTER PANEL: LIVE FLEET MANAGEMENT TABLE
        // ==========================================
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 233, 238), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblTableTitle = new JLabel("Global System Fleet Inventory", JLabel.LEFT);
        lblTableTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTableTitle.setForeground(new Color(51, 65, 85));
        centerPanel.add(lblTableTitle, BorderLayout.NORTH);

        // Core schema representation columns
        String[] columns = {"Plate Number", "Brand", "Model", "Daily Rate (LKR)", "Current Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Protect entries from direct modifications outside input workflows
            }
        };

        tblVehicles = new JTable(tableModel);
        tblVehicles.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblVehicles.setRowHeight(28);
        tblVehicles.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblVehicles.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // 📌 MEMORY ANCHOR: Temp Mock Data to be deleted during backend wiring phase!
        tableModel.addRow(new Object[]{"WP-CB-1234", "Toyota", "Prius", "8500.00", "Available"});
        tableModel.addRow(new Object[]{"WP-DA-5678", "Honda", "Civic", "9500.00", "Rented"});

        JScrollPane tableScrollPane = new JScrollPane(tblVehicles);
        centerPanel.add(tableScrollPane, BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // ==========================================
        // 3. EAST PANEL: ADD & MODIFY VEHICLE FORM ENGINE
        // ==========================================
        JPanel eastPanel = new JPanel(new GridBagLayout());
        eastPanel.setBackground(Color.WHITE);
        eastPanel.setPreferredSize(new Dimension(320, 0));
        eastPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 233, 238), 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.gridx = 0;

        JLabel lblFormHeader = new JLabel("Manage Inventory Vehicle", JLabel.CENTER);
        lblFormHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblFormHeader.setForeground(new Color(15, 23, 42));
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 12, 0);
        eastPanel.add(lblFormHeader, gbc);

        gbc.insets = new Insets(3, 0, 3, 0);

        // Plate Input
        JLabel lblPlate = new JLabel("Plate Number:");
        lblPlate.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridy = 1;
        eastPanel.add(lblPlate, gbc);

        txtPlateNumber = new JTextField();
        txtPlateNumber.setPreferredSize(new Dimension(0, 32));
        txtPlateNumber.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridy = 2;
        eastPanel.add(txtPlateNumber, gbc);

        // Brand Input
        JLabel lblBrand = new JLabel("Vehicle Brand:");
        lblBrand.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridy = 3;
        eastPanel.add(lblBrand, gbc);

        txtBrand = new JTextField();
        txtBrand.setPreferredSize(new Dimension(0, 32));
        txtBrand.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridy = 4;
        eastPanel.add(txtBrand, gbc);

        // Model Input
        JLabel lblModel = new JLabel("Vehicle Model:");
        lblModel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridy = 5;
        eastPanel.add(lblModel, gbc);

        txtModel = new JTextField();
        txtModel.setPreferredSize(new Dimension(0, 32));
        txtModel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridy = 6;
        eastPanel.add(txtModel, gbc);

        // Price Input
        JLabel lblRate = new JLabel("Daily Rental Rate (LKR):");
        lblRate.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridy = 7;
        eastPanel.add(lblRate, gbc);

        txtDailyRate = new JTextField();
        txtDailyRate.setPreferredSize(new Dimension(0, 32));
        txtDailyRate.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridy = 8;
        eastPanel.add(txtDailyRate, gbc);

        // Availability State Selector
        JLabel lblStatus = new JLabel("Operational Status:");
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridy = 9;
        eastPanel.add(lblStatus, gbc);

        cmbStatus = new JComboBox<>(new String[]{"Available", "Rented", "Maintenance"});
        cmbStatus.setPreferredSize(new Dimension(0, 32));
        cmbStatus.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbStatus.setBackground(Color.WHITE);
        gbc.gridy = 10;
        eastPanel.add(cmbStatus, gbc);

        // Separator line
        JSeparator formSep = new JSeparator();
        gbc.gridy = 11;
        gbc.insets = new Insets(12, 0, 12, 0);
        eastPanel.add(formSep, gbc);
        gbc.insets = new Insets(5, 0, 5, 0);

        // Action Controls Execution Triggers
        btnAddVehicle = new JButton("Register New Vehicle");
        btnAddVehicle.setPreferredSize(new Dimension(0, 38));
        btnAddVehicle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnAddVehicle.setBackground(new Color(22, 163, 74)); // Secure Green Action Color
        btnAddVehicle.setForeground(Color.WHITE);
        btnAddVehicle.setFocusPainted(false);
        btnAddVehicle.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 12;
        eastPanel.add(btnAddVehicle, gbc);

        btnUpdateStatus = new JButton("Update Selected Status");
        btnUpdateStatus.setPreferredSize(new Dimension(0, 38));
        btnUpdateStatus.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnUpdateStatus.setBackground(new Color(37, 99, 235)); // Modern Command Blue
        btnUpdateStatus.setForeground(Color.WHITE);
        btnUpdateStatus.setFocusPainted(false);
        btnUpdateStatus.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 13;
        eastPanel.add(btnUpdateStatus, gbc);

        mainPanel.add(eastPanel, BorderLayout.EAST);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            new AdminDashboard().setVisible(true);
        });
    }
}