package com.rental.ui;

import com.rental.config.DatabaseConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDashboard extends JFrame {

    private JTabbedPane tabbedPane;
    
    // Rental Orders Components
    private JTable tblBookings;
    private DefaultTableModel adminHistoryModel;
    private JButton btnApprove;
    private JButton btnReject;
    private JButton btnCompleteReturn;
    private JButton btnPrintInvoice;

    // Fleet Logistics Components
    private JTable tblVehicles;
    private DefaultTableModel vehicleModel;

    public AdminDashboard() {
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
        refreshAllData();
    }

    private void initComponents() {
        setTitle("DriveFlow Rentals - Admin Command Center");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 650);
        setLocationRelativeTo(null); // Center on screen

        // Main background panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(248, 250, 252));
        this.setContentPane(mainPanel);

        // --- 1. TOP HEADER BANNER ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(15, 23, 42)); // Deep Slate Dark Accent
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        JLabel lblTitle = new JLabel("Admin Control Dashboard");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);

        JButton btnLogout = new JButton("Logout");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.setBackground(new Color(239, 68, 68)); // Soft red
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            this.dispose();
        });

        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(btnLogout, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // --- 2. WORKSPACE TABBED PANELS ---
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));

        setupRentalOrdersTab();
        setupFleetLogisticsTab();

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
    }

    private void setupRentalOrdersTab() {
        JPanel ordersPanel = new JPanel(new BorderLayout());
        ordersPanel.setBackground(Color.WHITE);
        ordersPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Bookings JTable Architecture
        String[] columns = {"Booking ID", "Customer", "Vehicle", "Days", "Rate (LKR)", "Total (LKR)", "Status"};
        adminHistoryModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblBookings = new JTable(adminHistoryModel);
        tblBookings.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblBookings.setRowHeight(28);
        tblBookings.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        JScrollPane scrollPane = new JScrollPane(tblBookings);
        ordersPanel.add(scrollPane, BorderLayout.CENTER);

        // Lifecycle Action Button Panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        controlPanel.setBackground(Color.WHITE);

        btnApprove = new JButton("Approve Rental");
        styleButton(btnApprove, new Color(37, 99, 235)); // Blue

        btnReject = new JButton("Reject Rental");
        styleButton(btnReject, new Color(220, 38, 38)); // Red

        btnCompleteReturn = new JButton("Complete Return");
        styleButton(btnCompleteReturn, new Color(79, 70, 229)); // Indigo

        btnPrintInvoice = new JButton("Print Invoice");
        styleButton(btnPrintInvoice, new Color(16, 185, 129)); // Emerald Green for printing actions

        controlPanel.add(btnApprove);
        controlPanel.add(btnReject);
        controlPanel.add(btnCompleteReturn);
        controlPanel.add(btnPrintInvoice);
        ordersPanel.add(controlPanel, BorderLayout.SOUTH);

        // --- ACTION LISTENERS ---
        btnApprove.addActionListener(e -> updateBookingStatus("Approved"));
        btnReject.addActionListener(e -> updateBookingStatus("Rejected"));
        btnCompleteReturn.addActionListener(e -> updateBookingStatus("Returned"));
        
        // Print Invoice Trigger Pipeline
        btnPrintInvoice.addActionListener(e -> triggerInvoiceSpooler());

        tabbedPane.addTab("Rental Orders Log", ordersPanel);
    }

    private void setupFleetLogisticsTab() {
        JPanel fleetPanel = new JPanel(new BorderLayout());
        fleetPanel.setBackground(Color.WHITE);
        fleetPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        String[] columns = {"Vehicle ID", "Brand/Model", "Category", "Rate Per Day (LKR)", "Status"};
        vehicleModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblVehicles = new JTable(vehicleModel);
        tblVehicles.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblVehicles.setRowHeight(28);
        tblVehicles.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        JScrollPane scrollPane = new JScrollPane(tblVehicles);
        fleetPanel.add(scrollPane, BorderLayout.CENTER);

        tabbedPane.addTab("Fleet Logistics", fleetPanel);
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(140, 38));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // --- DATA FETCHING & STATE ENGINES ---

    private void refreshAllData() {
        loadBookingData();
        loadVehicleData();
    }

    private void loadBookingData() {
        adminHistoryModel.setRowCount(0);
        String query = "SELECT b.id, b.username, b.vehicle_name, b.rental_days, b.rate_per_day, b.total_amount, b.status FROM bookings b";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                adminHistoryModel.addRow(new Object[]{
                    rs.getString("id"),
                    rs.getString("username"),
                    rs.getString("vehicle_name"),
                    rs.getInt("rental_days"),
                    rs.getDouble("rate_per_day"),
                    rs.getDouble("total_amount"),
                    rs.getString("status")
                });
            }
        } catch (SQLException e) {
            System.out.println("Error Loading Bookings: " + e.getMessage());
        }
    }

    private void loadVehicleData() {
        vehicleModel.setRowCount(0);
        String query = "SELECT id, model, category, rate_per_day, status FROM vehicles";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                vehicleModel.addRow(new Object[]{
                    rs.getString("id"),
                    rs.getString("model"),
                    rs.getString("category"),
                    rs.getDouble("rate_per_day"),
                    rs.getString("status")
                });
            }
        } catch (SQLException e) {
            System.out.println("Error Loading Fleet: " + e.getMessage());
        }
    }

    private void updateBookingStatus(String newStatus) {
        int selectedRow = tblBookings.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an active order to update.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String bookingId = tblBookings.getValueAt(selectedRow, 0).toString();
        String updateQuery = "UPDATE bookings SET status = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {

            pstmt.setString(1, newStatus);
            pstmt.setString(2, bookingId);
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Order Status updated to: " + newStatus, "Status Synchronized", JOptionPane.INFORMATION_MESSAGE);
            refreshAllData(); // Refresh tables instantly

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database Modification Failure: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Pulls data directly out of the highlighted table row and spins up the Invoice Window
    private void triggerInvoiceSpooler() {
        int selectedRow = tblBookings.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please highlight a booking from the table to print.", "No Order Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Read types directly from row matrix mapping matching our columns setup
            String id       = tblBookings.getValueAt(selectedRow, 0).toString();
            String customer = tblBookings.getValueAt(selectedRow, 1).toString();
            String vehicle  = tblBookings.getValueAt(selectedRow, 2).toString();
            int days        = Integer.parseInt(tblBookings.getValueAt(selectedRow, 3).toString());
            double rate     = Double.parseDouble(tblBookings.getValueAt(selectedRow, 4).toString());
            double total    = Double.parseDouble(tblBookings.getValueAt(selectedRow, 5).toString());

            // Fire modal interface engine 
            InvoiceDialog invoice = new InvoiceDialog(this, id, customer, vehicle, days, rate, total);
            invoice.setVisible(true);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Data extraction error: " + ex.getMessage(), "Parsing Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new AdminDashboard().setVisible(true));
    }
}
