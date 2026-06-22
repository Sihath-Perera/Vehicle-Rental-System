package com.rental.ui;

import com.rental.config.DatabaseConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet; 
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

public class CustomerPortal extends JFrame {

    private CardLayout cardLayout;
    private JPanel cardsContainer;
    
    // Core Layout Components
    private JTable tblAvailableVehicles;
    private DefaultTableModel showroomModel;
    private JTable tblBookingHistory;
    private DefaultTableModel historyModel;
    
    // Date Input Fields
    private JTextField txtPickupDate;
    private JTextField txtReturnDate;
    
    private JButton btnBookVehicle;
    private JButton btnLogout;

    public CustomerPortal() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {}

        initComponents();
        loadAvailableVehicles(); 
        loadBookingHistory();
    }

    private void initComponents() {
        setTitle("Customer Portal - Dashboard Panel");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1050, 650);
        setResizable(true);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(new Color(244, 246, 249));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        this.setContentPane(mainPanel);

        // ==========================================
        // TOP CONTROL BANNER
        // ==========================================
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(30, 58, 138)); 
        topPanel.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        JLabel lblTitle = new JLabel("Customer Rental Experience Workspace", JLabel.LEFT);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);
        topPanel.add(lblTitle, BorderLayout.WEST);

        btnLogout = new JButton("Logout");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.setBackground(new Color(220, 38, 38)); 
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        topPanel.add(btnLogout, BorderLayout.EAST);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // ==========================================
        // LEFT SIDE NAVIGATION WORKSPACE
        // ==========================================
        JPanel sidebarPanel = new JPanel(new GridLayout(6, 1, 0, 10));
        sidebarPanel.setBackground(Color.WHITE);
        sidebarPanel.setPreferredSize(new Dimension(220, 0));
        sidebarPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 233, 238), 1),
                BorderFactory.createEmptyBorder(20, 15, 20, 15)
        ));

        JButton btnShowroomNav = new JButton("Vehicle Showroom");
        JButton btnProfileNav = new JButton("My Profile Info");
        JButton btnHistoryNav = new JButton("Rental History Log");

        JButton[] navButtons = {btnShowroomNav, btnProfileNav, btnHistoryNav};
        for (JButton btn : navButtons) {
            btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
            btn.setBackground(new Color(241, 245, 249));
            btn.setForeground(new Color(51, 65, 85));
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            sidebarPanel.add(btn);
        }

        mainPanel.add(sidebarPanel, BorderLayout.WEST);

        // ==========================================
        // CENTER CANVAS CONTROLLER (CARDLAYOUT ENGINE)
        // ==========================================
        cardLayout = new CardLayout();
        cardsContainer = new JPanel(cardLayout);
        cardsContainer.setBackground(Color.WHITE);

        // --- CARD 1: VEHICLE SHOWROOM ENGINE ---
        JPanel pnlShowroom = new JPanel(new BorderLayout(10, 10));
        pnlShowroom.setBackground(Color.WHITE);
        pnlShowroom.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel lblTableTitle = new JLabel("Select an available vehicle from our collection:", JLabel.LEFT);
        lblTableTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTableTitle.setForeground(new Color(51, 65, 85));
        pnlShowroom.add(lblTableTitle, BorderLayout.NORTH);

        String[] showroomCols = {"Plate Number", "Brand", "Model", "Daily Rate (LKR)"};
        showroomModel = new DefaultTableModel(showroomCols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblAvailableVehicles = new JTable(showroomModel);
        tblAvailableVehicles.setRowHeight(28);
        tblAvailableVehicles.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pnlShowroom.add(new JScrollPane(tblAvailableVehicles), BorderLayout.CENTER);

        // Booking Parameters Form Panel (Bottom of Showroom View)
        JPanel southActionPanel = new JPanel(new BorderLayout(10, 5));
        southActionPanel.setBackground(Color.WHITE);
        southActionPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JPanel inputsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        inputsPanel.setBackground(Color.WHITE);

        String defaultTodayStr = LocalDate.now().toString(); 

        inputsPanel.add(new JLabel("Pickup Date (YYYY-MM-DD):"));
        txtPickupDate = new JTextField(defaultTodayStr, 10);
        txtPickupDate.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        inputsPanel.add(txtPickupDate);

        inputsPanel.add(new JLabel("Return Date (YYYY-MM-DD):"));
        txtReturnDate = new JTextField(10);
        txtReturnDate.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        inputsPanel.add(txtReturnDate);

        southActionPanel.add(inputsPanel, BorderLayout.WEST);

        btnBookVehicle = new JButton("Place Pending Order");
        btnBookVehicle.setPreferredSize(new Dimension(200, 40));
        btnBookVehicle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnBookVehicle.setBackground(new Color(22, 163, 74));
        btnBookVehicle.setForeground(Color.WHITE);
        btnBookVehicle.setFocusPainted(false);
        southActionPanel.add(btnBookVehicle, BorderLayout.EAST);
        
        pnlShowroom.add(southActionPanel, BorderLayout.SOUTH);

        // --- CARD 2: MY PROFILE DASHBOARD VIEW ---
        JPanel pnlProfile = new JPanel(new GridBagLayout());
        pnlProfile.setBackground(Color.WHITE);
        pnlProfile.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints pGbc = new GridBagConstraints();
        pGbc.fill = GridBagConstraints.HORIZONTAL;
        pGbc.insets = new Insets(8, 0, 8, 0);
        pGbc.gridx = 0;

        JLabel lblProfHead = new JLabel("Account Profile Settings", JLabel.CENTER);
        lblProfHead.setFont(new Font("Segoe UI", Font.BOLD, 18));
        pGbc.gridy = 0; pGbc.gridwidth = 2;
        pnlProfile.add(lblProfHead, pGbc);
        pGbc.gridwidth = 1;

        pGbc.gridy = 1; pGbc.gridx = 0; pnlProfile.add(new JLabel("Full Registered Name:"), pGbc);
        pGbc.gridx = 1; pnlProfile.add(new JTextField("Pathum Srinath", 20), pGbc);
        pGbc.gridy = 2; pGbc.gridx = 0; pnlProfile.add(new JLabel("Contact Address Line:"), pGbc);
        pGbc.gridx = 1; pnlProfile.add(new JTextField("Mahabage, Sri Lanka", 20), pGbc);
        pGbc.gridy = 3; pGbc.gridx = 0; pnlProfile.add(new JLabel("License Credential:"), pGbc);
        pGbc.gridx = 1; pnlProfile.add(new JTextField("WP-B1234567", 20), pGbc);

        // --- CARD 3: RENTAL HISTORY LOG VIEW ---
        JPanel pnlHistory = new JPanel(new BorderLayout(10, 10));
        pnlHistory.setBackground(Color.WHITE);
        pnlHistory.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel lblHistTitle = new JLabel("Personal Historical System Bookings Ledger", JLabel.LEFT);
        lblHistTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pnlHistory.add(lblHistTitle, BorderLayout.NORTH);

        String[] histCols = {"Booking ID", "Vehicle Plate Ref", "Pickup Date", "Return Date", "Order Status"};
        historyModel = new DefaultTableModel(histCols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblBookingHistory = new JTable(historyModel);
        tblBookingHistory.setRowHeight(28);
        pnlHistory.add(new JScrollPane(tblBookingHistory), BorderLayout.CENTER);

        cardsContainer.add(pnlShowroom, "SHOWROOM");
        cardsContainer.add(pnlProfile, "PROFILE");
        cardsContainer.add(pnlHistory, "HISTORY");

        mainPanel.add(cardsContainer, BorderLayout.CENTER);

        // ==========================================
        // ACTION MECHANISMS
        // ==========================================
        btnShowroomNav.addActionListener(e -> cardLayout.show(cardsContainer, "SHOWROOM"));
        btnProfileNav.addActionListener(e -> cardLayout.show(cardsContainer, "PROFILE"));
        btnHistoryNav.addActionListener(e -> {
            loadBookingHistory();
            cardLayout.show(cardsContainer, "HISTORY");
        });

        btnBookVehicle.addActionListener(e -> performVehicleBooking());
        btnLogout.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            this.dispose();
        });
    }

    private void loadAvailableVehicles() {
        showroomModel.setRowCount(0); 
        String query = "SELECT * FROM vehicles WHERE status = 'Available'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                showroomModel.addRow(new Object[]{
                    rs.getString("plate_number"),
                    rs.getString("brand"),
                    rs.getString("model"),
                    rs.getDouble("daily_rate")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching showroom items: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadBookingHistory() {
        historyModel.setRowCount(0);
        String query = "SELECT booking_id, plate_number, pickup_date, return_date, booking_status FROM bookings ORDER BY booking_id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                historyModel.addRow(new Object[]{
                    rs.getInt("booking_id"),
                    rs.getString("plate_number"),
                    rs.getDate("pickup_date"),
                    rs.getDate("return_date"),
                    rs.getString("booking_status")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "History Sync Failed: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performVehicleBooking() {
    int selectedRow = tblAvailableVehicles.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please choose a vehicle from the catalog grid layout.", "No Car Selected", JOptionPane.WARNING_MESSAGE);
        return;
    }

    String pickupStr = txtPickupDate.getText().trim();
    String returnStr = txtReturnDate.getText().trim();

    if (pickupStr.isEmpty() || returnStr.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Please fill out both the Pickup and Return dates.", "Missing Parameters", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // --- ⚡ BUSINESS GUARDRAILS & DATE VALIDATION LOGIC ---
    LocalDate today = LocalDate.now();
    LocalDate pickupDate;
    LocalDate returnDate;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    try {
        pickupDate = LocalDate.parse(pickupStr, formatter);
        returnDate = LocalDate.parse(returnStr, formatter);
    } catch (DateTimeParseException ex) {
        JOptionPane.showMessageDialog(this, "Invalid Date Format! Please type using YYYY-MM-DD syntax.", "Formatting Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    if (pickupDate.isBefore(today)) {
        JOptionPane.showMessageDialog(this, "Pickup date cannot be placed in the past.", "Logistics Rule Exception", JOptionPane.WARNING_MESSAGE);
        return;
    }

    if (!returnDate.isAfter(pickupDate)) {
        JOptionPane.showMessageDialog(this, "Return date must take place at least one day after your pickup timestamp.", "Logistics Rule Exception", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // CRITICAL 3-DAY ADVANCED BOOKING LIMIT GUARDRAIL
    long daysInAdvance = ChronoUnit.DAYS.between(today, pickupDate);
    if (daysInAdvance > 3) {
        JOptionPane.showMessageDialog(this, 
            "Booking Blocked! You can only reserve vehicles up to 3 days in advance.\n" +
            "This ensures equal fleet accessibility. Selected date is " + daysInAdvance + " days out.", 
            "Queue Safety Violation", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // =========================================================
    // 📊 NEW: LIVE BILL CALCULATION ENGINE
    // =========================================================
    String plateNumber = showroomModel.getValueAt(selectedRow, 0).toString();
    String brandAndModel = showroomModel.getValueAt(selectedRow, 1).toString() + " " + showroomModel.getValueAt(selectedRow, 2).toString();
    
    // Extract the daily rate from the JTable cell
    double dailyRate = Double.parseDouble(showroomModel.getValueAt(selectedRow, 3).toString());
    
    // Compute exact delta days
    long totalDays = ChronoUnit.DAYS.between(pickupDate, returnDate);
    double totalCost = totalDays * dailyRate;

    // Build a styled, professional Invoice Summary Panel
    String invoiceSummary = String.format(
        "📄 RENTAL RESERVATION SUMMARY\n" +
        "-----------------------------------------------\n" +
        "Vehicle: %s\n" +
        "Plate Number: %s\n" +
        "Duration: %d Days\n" +
        "Timeline: From %s to %s\n\n" +
        "Daily Standard Rate: %,.2f LKR\n" +
        "ESTIMATED TOTAL DUE: %,.2f LKR\n" +
        "-----------------------------------------------\n" +
        "Would you like to place this pending order request?",
        brandAndModel, plateNumber, totalDays, pickupStr, returnStr, dailyRate, totalCost
    );

    // Prompt user to confirm before hitting Clever Cloud
    int userChoice = JOptionPane.showConfirmDialog(this, invoiceSummary, "Confirm Order & Estimated Bill", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
    
    if (userChoice != JOptionPane.YES_OPTION) {
        JOptionPane.showMessageDialog(this, "Reservation cancelled by user.", "Order Aborted", JOptionPane.WARNING_MESSAGE);
        return; // Break execution immediately out of the method
    }
    // =========================================================

    String bookingQuery = "INSERT INTO bookings (plate_number, pickup_date, return_date, booking_status) VALUES (?, ?, ?, 'Pending')";
    String vehicleUpdateQuery = "UPDATE vehicles SET status = 'Ordered' WHERE plate_number = ?";

    try (Connection conn = DatabaseConnection.getConnection()) {
        conn.setAutoCommit(false); 

            try (PreparedStatement bookingStmt = conn.prepareStatement(bookingQuery);
                 PreparedStatement vehicleStmt = conn.prepareStatement(vehicleUpdateQuery)) {

                bookingStmt.setString(1, plateNumber);
                bookingStmt.setDate(2, java.sql.Date.valueOf(pickupDate));
                bookingStmt.setDate(3, java.sql.Date.valueOf(returnDate));
                bookingStmt.executeUpdate();

                // Changes state to 'Ordered' so it drops from other customer screens
                vehicleStmt.setString(1, plateNumber);
                vehicleStmt.executeUpdate();

                conn.commit(); 
                JOptionPane.showMessageDialog(this, 
                    "Order Placed Successfully!\nYour reservation for " + brandAndModel + " is now pending.\n" +
                    "Please visit the store on " + pickupStr + " to complete verification and collect your vehicle.", 
                    "Order Logged", JOptionPane.INFORMATION_MESSAGE);
                
                txtReturnDate.setText(""); 
                loadAvailableVehicles(); 

            } catch (SQLException ex) {
                conn.rollback(); 
                throw ex;
            }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Transaction Aborted: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
    }
}

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new CustomerPortal().setVisible(true));
    }
}