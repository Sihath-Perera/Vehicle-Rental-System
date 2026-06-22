package com.rental.ui;

import com.rental.config.DatabaseConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdminDashboard extends JFrame {

    // Tab 1: Fleet Management Components
    private JTable tblVehicles;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> tableSorter;
    
    // Tab 2: New Booking Management Components
    private JTable tblBookings;
    private DefaultTableModel adminHistoryModel;
    private JButton btnApprove;
    private JButton btnReject;
    private JButton btnCompleteReturn;
    
    // Left Panel Components
    private JTextField txtSearch;
    private JComboBox<String> cmbFilterStatus;
    private JLabel lblTotalCount;
    private JLabel lblRentedCount;
    private JLabel lblMaintenanceCount;

    // Right Panel Components (Form)
    private JTextField txtPlateNumber;
    private JTextField txtBrand;
    private JTextField txtModel;
    private JTextField txtDailyRate;
    private JComboBox<String> cmbStatus;
    
    private JButton btnAddVehicle;
    private JButton btnUpdateStatus;
    private JButton btnLogout;

    public AdminDashboard() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {}

        initComponents();
        loadVehicleData(); 
        loadAdminBookingHistory(); // Added: Syncs booking requests on startup
    }

    private void initComponents() {
        setTitle("Admin Dashboard - Fleet Management Control");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1150, 680); 

        setResizable(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(new Color(244, 246, 249));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        this.setContentPane(mainPanel);

        // ==========================================
        // TOP HEADER BANNER
        // ==========================================
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(15, 23, 42)); 
        topPanel.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        JLabel lblAdminTitle = new JLabel("Administrative Control Panel", JLabel.LEFT);
        lblAdminTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblAdminTitle.setForeground(Color.WHITE);
        topPanel.add(lblAdminTitle, BorderLayout.WEST);

        btnLogout = new JButton("Logout");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.setBackground(new Color(220, 38, 38)); 
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        topPanel.add(btnLogout, BorderLayout.EAST);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // ==========================================
        // WEST PANEL (SEARCH, FILTER & METRICS)
        // ==========================================
        JPanel westPanel = new JPanel(new GridBagLayout());
        westPanel.setBackground(Color.WHITE);
        westPanel.setPreferredSize(new Dimension(260, 0));
        westPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 233, 238), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        GridBagConstraints wGbc = new GridBagConstraints();
        wGbc.fill = GridBagConstraints.HORIZONTAL;
        wGbc.insets = new Insets(5, 0, 5, 0);
        wGbc.gridx = 0;

        JLabel lblSearchHeader = new JLabel("Search & Filter Fleet", JLabel.CENTER);
        lblSearchHeader.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblSearchHeader.setForeground(new Color(15, 23, 42));
        wGbc.gridy = 0;
        wGbc.insets = new Insets(0, 0, 10, 0);
        westPanel.add(lblSearchHeader, wGbc);
        wGbc.insets = new Insets(4, 0, 4, 0);

        JLabel lblSearch = new JLabel("Live Text Search:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 12));
        wGbc.gridy = 1;
        westPanel.add(lblSearch, wGbc);

        txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(0, 32));
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        wGbc.gridy = 2;
        westPanel.add(txtSearch, wGbc);

        JLabel lblFilter = new JLabel("Isolate Status:");
        lblFilter.setFont(new Font("Segoe UI", Font.BOLD, 12));
        wGbc.gridy = 3;
        wGbc.insets = new Insets(10, 0, 4, 0);
        westPanel.add(lblFilter, wGbc);
        wGbc.insets = new Insets(4, 0, 4, 0);

        cmbFilterStatus = new JComboBox<>(new String[]{"ALL", "Available", "Rented", "Maintenance"});
        cmbFilterStatus.setPreferredSize(new Dimension(0, 32));
        cmbFilterStatus.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbFilterStatus.setBackground(Color.WHITE);
        wGbc.gridy = 4;
        westPanel.add(cmbFilterStatus, wGbc);

        JSeparator westSep = new JSeparator();
        wGbc.gridy = 5;
        wGbc.insets = new Insets(15, 0, 15, 0);
        westPanel.add(westSep, wGbc);
        wGbc.insets = new Insets(4, 0, 4, 0);

        JLabel lblMetricsTitle = new JLabel("Live Fleet Overview", JLabel.CENTER);
        lblMetricsTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblMetricsTitle.setForeground(new Color(51, 65, 85));
        wGbc.gridy = 6;
        wGbc.insets = new Insets(0, 0, 8, 0);
        westPanel.add(lblMetricsTitle, wGbc);
        wGbc.insets = new Insets(4, 0, 4, 0);

        lblTotalCount = new JLabel("Total Registered: 0");
        lblTotalCount.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        wGbc.gridy = 7;
        westPanel.add(lblTotalCount, wGbc);

        lblRentedCount = new JLabel("Currently Rented: 0");
        lblRentedCount.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblRentedCount.setForeground(new Color(37, 99, 235));
        wGbc.gridy = 8;
        westPanel.add(lblRentedCount, wGbc);

        lblMaintenanceCount = new JLabel("In Maintenance: 0");
        lblMaintenanceCount.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblMaintenanceCount.setForeground(new Color(220, 38, 38));
        wGbc.gridy = 9;
        westPanel.add(lblMaintenanceCount, wGbc);

        mainPanel.add(westPanel, BorderLayout.WEST);

        // =========================================================
        // WORKSPACE WORKFLOW LAYER: CENTRAL TABBED WRAPPER
        // =========================================================
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));

        // --- TAB 1: FLEET INVENTORY PANEL ---
        JPanel fleetTabPanel = new JPanel(new BorderLayout(10, 10));
        fleetTabPanel.setBackground(Color.WHITE);
        fleetTabPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblTableTitle = new JLabel("Global System Fleet Inventory", JLabel.LEFT);
        lblTableTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTableTitle.setForeground(new Color(51, 65, 85));
        fleetTabPanel.add(lblTableTitle, BorderLayout.NORTH);

        String[] columns = {"Plate Number", "Brand", "Model", "Daily Rate (LKR)", "Current Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tableSorter = new TableRowSorter<>(tableModel);
        tblVehicles = new JTable(tableModel);
        tblVehicles.setRowSorter(tableSorter);
        tblVehicles.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblVehicles.setRowHeight(28);
        tblVehicles.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblVehicles.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane tableScrollPane = new JScrollPane(tblVehicles);
        fleetTabPanel.add(tableScrollPane, BorderLayout.CENTER);
        tabbedPane.addTab("Fleet Inventory Log", fleetTabPanel);

        // --- TAB 2: RENTAL ORDERS & ACTIONS PANEL ---
        JPanel bookingsTabPanel = new JPanel(new BorderLayout(15, 15));
        bookingsTabPanel.setBackground(Color.WHITE);
        bookingsTabPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblBookingsTitle = new JLabel("Live Booking Requests & Pipeline Processing", JLabel.LEFT);
        lblBookingsTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblBookingsTitle.setForeground(new Color(51, 65, 85));
        bookingsTabPanel.add(lblBookingsTitle, BorderLayout.NORTH);

        String[] bookingColumns = {"Booking ID", "Plate Number", "Pickup Date", "Return Date", "Order Status"};
        adminHistoryModel = new DefaultTableModel(bookingColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tblBookings = new JTable(adminHistoryModel);
        tblBookings.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblBookings.setRowHeight(28);
        tblBookings.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblBookings.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane bookingsScrollPane = new JScrollPane(tblBookings);
        bookingsTabPanel.add(bookingsScrollPane, BorderLayout.CENTER);

        // Grid Action Panel for State Machine Buttons
        JPanel bookingActionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        bookingActionPanel.setBackground(Color.WHITE);

        btnApprove = new JButton("Approve Reservation");
        btnApprove.setPreferredSize(new Dimension(170, 38));
        btnApprove.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnApprove.setBackground(new Color(22, 163, 74));
        btnApprove.setForeground(Color.WHITE);
        btnApprove.setFocusPainted(false);
        btnApprove.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnReject = new JButton("Reject Request");
        btnReject.setPreferredSize(new Dimension(140, 38));
        btnReject.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnReject.setBackground(new Color(220, 38, 38));
        btnReject.setForeground(Color.WHITE);
        btnReject.setFocusPainted(false);
        btnReject.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnCompleteReturn = new JButton("Finalize Car Return");
        btnCompleteReturn.setPreferredSize(new Dimension(170, 38));
        btnCompleteReturn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCompleteReturn.setBackground(new Color(37, 99, 235));
        btnCompleteReturn.setForeground(Color.WHITE);
        btnCompleteReturn.setFocusPainted(false);
        btnCompleteReturn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        bookingActionPanel.add(btnApprove);
        bookingActionPanel.add(btnReject);
        bookingActionPanel.add(btnCompleteReturn);
        bookingsTabPanel.add(bookingActionPanel, BorderLayout.SOUTH);

        tabbedPane.addTab("Rental Orders & Actions", bookingsTabPanel);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        // ==========================================
        // EAST PANEL: ADD & MODIFY VEHICLE FORM ENGINE
        // ==========================================
        JPanel eastPanel = new JPanel(new GridBagLayout());
        eastPanel.setBackground(Color.WHITE);
        eastPanel.setPreferredSize(new Dimension(280, 0));
        eastPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 233, 238), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.gridx = 0;

        JLabel lblFormHeader = new JLabel("Manage Vehicle Details", JLabel.CENTER);
        lblFormHeader.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblFormHeader.setForeground(new Color(15, 23, 42));
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 10, 0);
        eastPanel.add(lblFormHeader, gbc);
        gbc.insets = new Insets(3, 0, 3, 0);

        JLabel lblPlate = new JLabel("Plate Number:");
        lblPlate.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridy = 1;
        eastPanel.add(lblPlate, gbc);

        txtPlateNumber = new JTextField();
        txtPlateNumber.setPreferredSize(new Dimension(0, 32));
        gbc.gridy = 2;
        eastPanel.add(txtPlateNumber, gbc);

        JLabel lblBrand = new JLabel("Vehicle Brand:");
        lblBrand.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridy = 3;
        eastPanel.add(lblBrand, gbc);

        txtBrand = new JTextField();
        txtBrand.setPreferredSize(new Dimension(0, 32));
        gbc.gridy = 4;
        eastPanel.add(txtBrand, gbc);

        JLabel lblModel = new JLabel("Vehicle Model:");
        lblModel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridy = 5;
        eastPanel.add(lblModel, gbc);

        txtModel = new JTextField();
        txtModel.setPreferredSize(new Dimension(0, 32));
        gbc.gridy = 6;
        eastPanel.add(txtModel, gbc);

        JLabel lblRate = new JLabel("Daily Rental Rate (LKR):");
        lblRate.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridy = 7;
        eastPanel.add(lblRate, gbc);

        txtDailyRate = new JTextField();
        txtDailyRate.setPreferredSize(new Dimension(0, 32));
        gbc.gridy = 8;
        eastPanel.add(txtDailyRate, gbc);

        JLabel lblStatus = new JLabel("Operational Status:");
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridy = 9;
        eastPanel.add(lblStatus, gbc);

        cmbStatus = new JComboBox<>(new String[]{"Available", "Rented", "Maintenance", "Ordered"});
        cmbStatus.setPreferredSize(new Dimension(0, 32));
        cmbStatus.setBackground(Color.WHITE);
        gbc.gridy = 10;
        eastPanel.add(cmbStatus, gbc);

        JSeparator formSep = new JSeparator();
        gbc.gridy = 11;
        gbc.insets = new Insets(10, 0, 10, 0);
        eastPanel.add(formSep, gbc);
        gbc.insets = new Insets(4, 0, 4, 0);

        btnAddVehicle = new JButton("Register New Vehicle");
        btnAddVehicle.setPreferredSize(new Dimension(0, 38));
        btnAddVehicle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnAddVehicle.setBackground(new Color(22, 163, 74)); 
        btnAddVehicle.setForeground(Color.WHITE);
        btnAddVehicle.setFocusPainted(false);
        btnAddVehicle.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 12;
        eastPanel.add(btnAddVehicle, gbc);

        btnUpdateStatus = new JButton("Update Selected Status");
        btnUpdateStatus.setPreferredSize(new Dimension(0, 38));
        btnUpdateStatus.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnUpdateStatus.setBackground(new Color(37, 99, 235)); 
        btnUpdateStatus.setForeground(Color.WHITE);
        btnUpdateStatus.setFocusPainted(false);
        btnUpdateStatus.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 13;
        eastPanel.add(btnUpdateStatus, gbc);

        mainPanel.add(eastPanel, BorderLayout.EAST);

        // ==========================================
        // WIRE UP HOOKS & CONTROLS
        // ==========================================
        setupFilteringLogics();

        btnAddVehicle.addActionListener(e -> performVehicleRegistration());
        btnUpdateStatus.addActionListener(e -> performStatusUpdate());
        btnLogout.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            this.dispose();
        });
        
        tblVehicles.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = tblVehicles.getSelectedRow();
            if (selectedRow != -1) {
                int modelRow = tblVehicles.convertRowIndexToModel(selectedRow);
                txtPlateNumber.setText(tableModel.getValueAt(modelRow, 0).toString());
                txtBrand.setText(tableModel.getValueAt(modelRow, 1).toString());
                txtModel.setText(tableModel.getValueAt(modelRow, 2).toString());
                txtDailyRate.setText(tableModel.getValueAt(modelRow, 3).toString());
                cmbStatus.setSelectedItem(tableModel.getValueAt(modelRow, 4).toString());
            }
        });

        // --- ACTION LISTENERS FOR PIPELINE PROCESSING BUTTONS ---
        btnApprove.addActionListener(e -> {
            int selectedRow = tblBookings.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a pending reservation from the table grid first.", "Selection Missing", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int bookingId = Integer.parseInt(adminHistoryModel.getValueAt(selectedRow, 0).toString());
            String currentStatus = adminHistoryModel.getValueAt(selectedRow, 4).toString();
            if (!currentStatus.equalsIgnoreCase("Pending")) {
                JOptionPane.showMessageDialog(this, "Action Blocked! You can only approve entries that are currently 'Pending'.", "Invalid Action", JOptionPane.WARNING_MESSAGE);
                return;
            }
            approveBooking(bookingId);
        });

        btnReject.addActionListener(e -> {
            int selectedRow = tblBookings.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select an entry from the grid layout to reject.", "Selection Missing", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int bookingId = Integer.parseInt(adminHistoryModel.getValueAt(selectedRow, 0).toString());
            String plateNumber = adminHistoryModel.getValueAt(selectedRow, 1).toString();
            String currentStatus = adminHistoryModel.getValueAt(selectedRow, 4).toString();
            if (!currentStatus.equalsIgnoreCase("Pending")) {
                JOptionPane.showMessageDialog(this, "Only 'Pending' requests can be actively rejected.", "Invalid Action", JOptionPane.WARNING_MESSAGE);
                return;
            }
            processVehicleReleaseOrReturn(bookingId, plateNumber, "Cancelled");
        });

        btnCompleteReturn.addActionListener(e -> {
            int selectedRow = tblBookings.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Select the active running rental being returned to the garage.", "Selection Missing", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int bookingId = Integer.parseInt(adminHistoryModel.getValueAt(selectedRow, 0).toString());
            String plateNumber = adminHistoryModel.getValueAt(selectedRow, 1).toString();
            String currentStatus = adminHistoryModel.getValueAt(selectedRow, 4).toString();
            if (!currentStatus.equalsIgnoreCase("Approved")) {
                JOptionPane.showMessageDialog(this, "You can only close out rentals that are actively 'Approved'.", "Invalid Action", JOptionPane.WARNING_MESSAGE);
                return;
            }
            processVehicleReleaseOrReturn(bookingId, plateNumber, "Completed");
        });
    }

    private void setupFilteringLogics() {
        DocumentListener searchListener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { executeCombinedFilter(); }
            public void removeUpdate(DocumentEvent e) { executeCombinedFilter(); }
            public void changedUpdate(DocumentEvent e) { executeCombinedFilter(); }
        };
        txtSearch.getDocument().addDocumentListener(searchListener);
        cmbFilterStatus.addActionListener(e -> executeCombinedFilter());
    }

    private void executeCombinedFilter() {
        String textText = txtSearch.getText().trim();
        String statusText = cmbFilterStatus.getSelectedItem().toString();

        List<RowFilter<Object, Object>> filters = new ArrayList<>();
        
        if (!textText.isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + textText));
        }
        if (!statusText.equals("ALL")) {
            filters.add(RowFilter.regexFilter("^" + statusText + "$", 4));
        }

        if (filters.isEmpty()) {
            tableSorter.setRowFilter(null);
        } else {
            tableSorter.setRowFilter(RowFilter.andFilter(filters));
        }
    }

    private void recalculateQuickStats() {
        int total = tableModel.getRowCount();
        int rented = 0;
        int maintenance = 0;

        for (int i = 0; i < total; i++) {
            String stat = tableModel.getValueAt(i, 4).toString();
            if (stat.equalsIgnoreCase("Rented") || stat.equalsIgnoreCase("Ordered")) rented++;
            else if (stat.equalsIgnoreCase("Maintenance")) maintenance++;
        }

        lblTotalCount.setText("Total Registered: " + total);
        lblRentedCount.setText("Currently Active: " + rented);
        lblMaintenanceCount.setText("In Maintenance: " + maintenance);
    }

    private void loadVehicleData() {
        tableModel.setRowCount(0); 
        String query = "SELECT * FROM vehicles";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("plate_number"),
                    rs.getString("brand"),
                    rs.getString("model"),
                    rs.getDouble("daily_rate"),
                    rs.getString("status")
                });
            }
            recalculateQuickStats();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load vehicles: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performVehicleRegistration() {
        String plate = txtPlateNumber.getText().trim();
        String brand = txtBrand.getText().trim();
        String model = txtModel.getText().trim();
        String rateStr = txtDailyRate.getText().trim();
        String status = cmbStatus.getSelectedItem().toString();

        if (plate.isEmpty() || brand.isEmpty() || model.isEmpty() || rateStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All vehicle registration fields are required.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double dailyRate;
        try {
            dailyRate = Double.parseDouble(rateStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Daily rate must be a valid numeric price.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String query = "INSERT INTO vehicles (plate_number, brand, model, daily_rate, status) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, plate);
            pstmt.setString(2, brand);
            pstmt.setString(3, model);
            pstmt.setDouble(4, dailyRate);
            pstmt.setString(5, status);

            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Vehicle registered successfully inside active inventory!");
            
            clearFormFields();
            loadVehicleData(); 

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Registration failed: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performStatusUpdate() {
        int selectedRow = tblVehicles.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a vehicle from the table grid first to update.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = tblVehicles.convertRowIndexToModel(selectedRow);
        String plate = tableModel.getValueAt(modelRow, 0).toString();
        String newStatus = cmbStatus.getSelectedItem().toString();

        String query = "UPDATE vehicles SET status = ? WHERE plate_number = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, newStatus);
            pstmt.setString(2, plate);

            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Operational status updated for vehicle: " + plate);
            
            clearFormFields();
            loadVehicleData(); 

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Update failed: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void approveBooking(int bookingId) {
        String query = "UPDATE bookings SET booking_status = 'Approved' WHERE booking_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, bookingId);
            int rowsUpdated = pstmt.executeUpdate();
            
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "Reservation ID " + bookingId + " has been successfully APPROVED.", "Status Updated", JOptionPane.INFORMATION_MESSAGE);
                loadAdminBookingHistory(); 
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadAdminBookingHistory() {
        adminHistoryModel.setRowCount(0); 
        String query = "SELECT booking_id, plate_number, pickup_date, return_date, booking_status FROM bookings ORDER BY booking_id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                adminHistoryModel.addRow(new Object[]{
                    rs.getInt("booking_id"),
                    rs.getString("plate_number"),
                    rs.getDate("pickup_date"),
                    rs.getDate("return_date"),
                    rs.getString("booking_status")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Admin Table Sync Failed: " + e.getMessage(), 
                                          "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void processVehicleReleaseOrReturn(int bookingId, String plateNumber, String targetBookingStatus) {
        String bookingQuery = "UPDATE bookings SET booking_status = ? WHERE booking_id = ?";
        String vehicleQuery = "UPDATE vehicles SET status = 'Available' WHERE plate_number = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false); 

            try (PreparedStatement bookingStmt = conn.prepareStatement(bookingQuery);
                 PreparedStatement vehicleStmt = conn.prepareStatement(vehicleQuery)) {

                bookingStmt.setString(1, targetBookingStatus);
                bookingStmt.setInt(2, bookingId);
                bookingStmt.executeUpdate();

                vehicleStmt.setString(1, plateNumber);
                vehicleStmt.executeUpdate();

                conn.commit(); 

                String msg = targetBookingStatus.equals("Completed") ? "Vehicle return logged. Transaction finalized." : "Reservation rejected. Fleet item freed.";
                JOptionPane.showMessageDialog(this, msg, "Process Success", JOptionPane.INFORMATION_MESSAGE);

                loadAdminBookingHistory(); 
                loadVehicleData(); // Synchronizes the inventory metrics tab seamlessly!

            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Transaction Aborted: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFormFields() {
        txtPlateNumber.setText("");
        txtBrand.setText("");
        txtModel.setText("");
        txtDailyRate.setText("");
        cmbStatus.setSelectedIndex(0);
        tblVehicles.clearSelection();
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new AdminDashboard().setVisible(true));
    }
}