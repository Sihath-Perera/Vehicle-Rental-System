package com.rental.ui;

import com.rental.controller.RentalController;
import com.rental.config.DatabaseConnection;
import com.rental.entities.Customer;
import com.rental.entities.Vehicle;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.sql.*;
import java.util.List;

public class CustomerPortal extends JFrame {
    private Customer currentCustomer;
    private RentalController controller;

    private JTabbedPane tabbedPane;
    private DefaultTableModel fleetModel, myBookingsModel;
    private JTable fleetTable, myBookingsTable;
    
    public CustomerPortal(Customer customer) {
        this.currentCustomer = customer;
        this.controller = new RentalController();
        initializeUI();
        refreshData();
    }

    private void initializeUI() {
        setTitle("DriveFlow Rentals - Welcome, " + currentCustomer.getUsername());
        setSize(950, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header Style with Logout
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(15, 23, 42)); // Dark Slate
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel lblWelcome = new JLabel("DRIVEFLOW PORTAL | Active User: " + currentCustomer.getUsername().toUpperCase());
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblWelcome.setForeground(Color.WHITE);
        header.add(lblWelcome, BorderLayout.WEST);
        
        // Logout Button
        JButton btnLogout = new JButton("Logout");
        btnLogout.setBackground(new Color(220, 38, 38)); // Danger Red
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.setFocusPainted(false);
        btnLogout.addActionListener(e -> {
            this.dispose(); // Close Portal
            new LoginFrame().setVisible(true); // Open Login
        });
        header.add(btnLogout, BorderLayout.EAST);
        
        add(header, BorderLayout.NORTH);

        // Core Layout Tabs
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        setupFleetTab();
        setupMyBookingsTab();
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    // Helper method to make tables look modern
    private void applyModernTableStyle(JTable table) {
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(241, 245, 249));
        table.getTableHeader().setForeground(new Color(15, 23, 42));
        table.setSelectionBackground(new Color(226, 232, 240));
        table.setSelectionForeground(Color.BLACK);
        table.setShowVerticalLines(false);
    }

    private void setupFleetTab() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // FIX: Override isCellEditable to prevent double-click typing
        fleetModel = new DefaultTableModel(new String[]{"Plate Number", "Brand", "Model", "Daily Rate (LKR)"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        fleetTable = new JTable(fleetModel);
        applyModernTableStyle(fleetTable);
        
        JButton btnBook = new JButton("Book Selected Vehicle");
        btnBook.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnBook.setBackground(new Color(37, 99, 235));
        btnBook.setForeground(Color.WHITE);
        btnBook.setFocusPainted(false);
        btnBook.addActionListener(e -> handleBooking());
        
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        actionPanel.add(btnBook);

        panel.add(new JScrollPane(fleetTable), BorderLayout.CENTER);
        panel.add(actionPanel, BorderLayout.SOUTH);
        tabbedPane.addTab("Available Fleet", panel);
    }

    private void setupMyBookingsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // FIX: Prevent editing
        myBookingsModel = new DefaultTableModel(new String[]{"Booking ID", "Vehicle Plate", "Start Date", "End Date", "Status", "Total Cost (LKR)"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        myBookingsTable = new JTable(myBookingsModel);
        applyModernTableStyle(myBookingsTable);
        
        JButton btnInvoice = new JButton("View & Print Invoice");
        btnInvoice.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnInvoice.setBackground(new Color(16, 185, 129));
        btnInvoice.setForeground(Color.WHITE);
        btnInvoice.setFocusPainted(false);
        btnInvoice.addActionListener(e -> handleOpenInvoice());
        
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        actionPanel.add(btnInvoice);

        panel.add(new JScrollPane(myBookingsTable), BorderLayout.CENTER);
        panel.add(actionPanel, BorderLayout.SOUTH);
        tabbedPane.addTab("My Bookings History", panel);
    }

    private void handleBooking() {
        int row = fleetTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a vehicle from the fleet map.");
            return;
        }

        String plate = (String) fleetModel.getValueAt(row, 0);
        double rate = (double) fleetModel.getValueAt(row, 3);

        String startStr = JOptionPane.showInputDialog(this, "Enter Start Date (YYYY-MM-DD):");
        String endStr = JOptionPane.showInputDialog(this, "Enter End Date (YYYY-MM-DD):");

        if (startStr == null || endStr == null || startStr.isEmpty() || endStr.isEmpty()) return;

        try {
            Date start = Date.valueOf(startStr);
            Date end = Date.valueOf(endStr);
            long diff = end.getTime() - start.getTime();
            int days = (int) (diff / (1000 * 60 * 60 * 24));
            
            if (days <= 0) {
                JOptionPane.showMessageDialog(this, "End date must be after start date.", "Date Logic Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            double totalCost = days * rate;

            if (controller.createRentalRecord(currentCustomer.getUserId(), plate, start, end, totalCost)) {
                JOptionPane.showMessageDialog(this, "Booking submitted! Awaiting corporate verification.");
                refreshData();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Formatting Error: Use YYYY-MM-DD format.", "Parse Failure", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleOpenInvoice() {
        int row = myBookingsTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an active or paid transaction row to generate receipt.");
            return;
        }

        String bookingId = String.valueOf(myBookingsModel.getValueAt(row, 0));
        String vehiclePlate = (String) myBookingsModel.getValueAt(row, 1);
        double totalCost = (double) myBookingsModel.getValueAt(row, 5);

        int simulatedDays = 1; 
        double simulatedRate = totalCost;

        // Ensure InvoiceDialog exists in your project or this will throw an error
        InvoiceDialog receipt = new InvoiceDialog(this, bookingId, currentCustomer.getUsername().toUpperCase(), vehiclePlate, simulatedDays, simulatedRate, totalCost);
        receipt.setVisible(true);
    }

    private void refreshData() {
        fleetModel.setRowCount(0);
        myBookingsModel.setRowCount(0);

        try {
            List<Vehicle> availableCars = controller.getVehiclesByStatus("Available");
            for (Vehicle v : availableCars) {
                fleetModel.addRow(new Object[]{v.getPlateNumber(), v.getBrand(), v.getModel(), v.getDailyRate()});
            }

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT * FROM rental_records WHERE customer_id = ?")) {
                stmt.setInt(1, currentCustomer.getUserId());
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    myBookingsModel.addRow(new Object[]{
                        rs.getInt("rental_id"), 
                        rs.getString("vehicle_plate"), 
                        rs.getDate("start_date"), 
                        rs.getDate("end_date"), 
                        rs.getString("status"), 
                        rs.getDouble("total_cost")
                    });
                }
            }
        } catch (SQLException ex) {
            System.err.println("Database Sync Warning: " + ex.getMessage());
        }
    }
}