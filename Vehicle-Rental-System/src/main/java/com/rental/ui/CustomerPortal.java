package com.rental.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class CustomerPortal extends JFrame {

    // Core variables precisely matching our backend data mapping requirements
    private JTable tblAvailableVehicles;
    private DefaultTableModel tableModel;
    private JTextField txtPickupDate;
    private JTextField txtReturnDate;
    private JLabel lblTotalCost;
    private JButton btnConfirmRental;
    private JButton btnLogout;

    public CustomerPortal() {
        // Look & Feel synchronization - Keeps styling identical across all team laptops
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
        setTitle("Customer Portal - Vehicle Booking Catalog");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 600);
        setResizable(false);
        setLocationRelativeTo(null); // Centers window on screen

        // Main Layout (BorderLayout splits control panels cleanly)
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(new Color(244, 246, 249)); // Soft gray/blue canvas
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        this.setContentPane(mainPanel);

        // ==========================================
        // 1. TOP HEADER PANEL
        // ==========================================
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(30, 58, 138)); // Matching corporate dark blue
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblWelcome = new JLabel("Welcome to Your Dashboard", JLabel.LEFT);
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblWelcome.setForeground(Color.WHITE);
        topPanel.add(lblWelcome, BorderLayout.WEST);

        btnLogout = new JButton("Logout");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.setBackground(new Color(220, 38, 38)); // Vibrant red notification tint
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        topPanel.add(btnLogout, BorderLayout.EAST);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // ==========================================
        // 2. CENTER PANEL: FLEET SHOWCASE CATALOG
        // ==========================================
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 233, 238), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblCatalogTitle = new JLabel("Available Fleet Catalog", JLabel.LEFT);
        lblCatalogTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblCatalogTitle.setForeground(new Color(51, 65, 85));
        centerPanel.add(lblCatalogTitle, BorderLayout.NORTH);

        // Define explicit column architecture for the data mapping rows
        String[] columns = {"Plate Number", "Brand", "Model", "Daily Rate (LKR)", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Prevent users from altering database fields directly inside rows
            }
        };

        tblAvailableVehicles = new JTable(tableModel);
        tblAvailableVehicles.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblAvailableVehicles.setRowHeight(28);
        tblAvailableVehicles.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblAvailableVehicles.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Temp Mock Data to prove visual mapping works perfectly before controller binding
        tableModel.addRow(new Object[]{"WP-CB-1234", "Toyota", "Prius", "8500.00", "Available"});
        tableModel.addRow(new Object[]{"WP-DA-5678", "Honda", "Civic", "9500.00", "Available"});

        JScrollPane tableScrollPane = new JScrollPane(tblAvailableVehicles);
        centerPanel.add(tableScrollPane, BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // ==========================================
        // 3. RIGHT PANEL: RESERVATION & CALCULATION ENGINE
        // ==========================================
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setPreferredSize(new Dimension(300, 0));
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 233, 238), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.gridx = 0;

        JLabel lblBookingHeader = new JLabel("Book Your Ride", JLabel.CENTER);
        lblBookingHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblBookingHeader.setForeground(new Color(30, 58, 138));
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 15, 0);
        rightPanel.add(lblBookingHeader, gbc);

        gbc.insets = new Insets(4, 0, 4, 0);

        // Pickup Inputs
        JLabel lblPickup = new JLabel("Pickup Date (YYYY-MM-DD):");
        lblPickup.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridy = 1;
        rightPanel.add(lblPickup, gbc);

        txtPickupDate = new JTextField();
        txtPickupDate.setPreferredSize(new Dimension(0, 32));
        txtPickupDate.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridy = 2;
        rightPanel.add(txtPickupDate, gbc);

        // Return Inputs
        JLabel lblReturn = new JLabel("Return Date (YYYY-MM-DD):");
        lblReturn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridy = 3;
        rightPanel.add(lblReturn, gbc);

        txtReturnDate = new JTextField();
        txtReturnDate.setPreferredSize(new Dimension(0, 32));
        txtReturnDate.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridy = 4;
        rightPanel.add(txtReturnDate, gbc);

        // Cost Calculation Showcase
        JSeparator separator = new JSeparator();
        gbc.gridy = 5;
        gbc.insets = new Insets(15, 0, 15, 0);
        rightPanel.add(separator, gbc);
        gbc.insets = new Insets(4, 0, 4, 0);

        JLabel lblTotalLabel = new JLabel("Estimated Total Cost:", JLabel.CENTER);
        lblTotalLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblTotalLabel.setForeground(Color.GRAY);
        gbc.gridy = 6;
        rightPanel.add(lblTotalLabel, gbc);

        lblTotalCost = new JLabel("0.00 LKR", JLabel.CENTER);
        lblTotalCost.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTotalCost.setForeground(new Color(22, 163, 74)); // Green color accent for currency calculations
        gbc.gridy = 7;
        gbc.insets = new Insets(0, 0, 15, 0);
        rightPanel.add(lblTotalCost, gbc);

        // Action Booking Trigger
        btnConfirmRental = new JButton("Confirm Booking");
        btnConfirmRental.setPreferredSize(new Dimension(0, 40));
        btnConfirmRental.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnConfirmRental.setBackground(new Color(37, 99, 235)); // Primary Action Blue Accent
        btnConfirmRental.setForeground(Color.WHITE);
        btnConfirmRental.setFocusPainted(false);
        btnConfirmRental.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 8;
        gbc.insets = new Insets(10, 0, 0, 0);
        rightPanel.add(btnConfirmRental, gbc);

        mainPanel.add(rightPanel, BorderLayout.EAST);
    }

    // Main deployment mechanism enabling instant run capabilities right inside NetBeans
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            new CustomerPortal().setVisible(true);
        });
    }
}