package com.rental.ui;

import com.rental.config.DatabaseConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerPortal extends JFrame {

    private CardLayout cardLayout;
    private JPanel cardsContainer;
    
    // Core Layout Components
    private JTable tblAvailableVehicles;
    private DefaultTableModel showroomModel;
    private JTable tblBookingHistory;
    private DefaultTableModel historyModel;
    
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
        setSize(1050, 620);
        setResizable(false);
        setLocationRelativeTo(null);

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

        JButton btnShowroomNav = new JButton("🛒 Vehicle Showroom");
        JButton btnProfileNav = new JButton("👤 My Profile Info");
        JButton btnHistoryNav = new JButton("📜 Rental History Log");

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

        JPanel southActionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        southActionPanel.setBackground(Color.WHITE);
        btnBookVehicle = new JButton("Confirm Booking Reservation");
        btnBookVehicle.setPreferredSize(new Dimension(240, 40));
        btnBookVehicle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnBookVehicle.setBackground(new Color(22, 163, 74));
        btnBookVehicle.setForeground(Color.WHITE);
        btnBookVehicle.setFocusPainted(false);
        southActionPanel.add(btnBookVehicle);
        pnlShowroom.add(southActionPanel, BorderLayout.SOUTH);

        // --- CARD 2: MY PROFILE CUSTOM DASHBOARD VIEW ---
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

        String[] histCols = {"Booking ID", "Vehicle Plate Ref", "Log Recorded Date"};
        historyModel = new DefaultTableModel(histCols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblBookingHistory = new JTable(historyModel);
        tblBookingHistory.setRowHeight(28);
        pnlHistory.add(new JScrollPane(tblBookingHistory), BorderLayout.CENTER);

        // Add subcomponents into Card stack layout map
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
            JOptionPane.showMessageDialog(this, "Error fetching catalog items: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadBookingHistory() {
        historyModel.setRowCount(0);
        String query = "SELECT * FROM bookings ORDER BY booking_id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                historyModel.addRow(new Object[]{
                    rs.getInt("booking_id"),
                    rs.getString("plate_number"),
                    rs.getTimestamp("booking_date")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "History Sync Failed: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performVehicleBooking() {
        int selectedRow = tblAvailableVehicles.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please choose an available vehicle from the catalog grid.", "No Car Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String plateNumber = showroomModel.getValueAt(selectedRow, 0).toString();
        String brandAndModel = showroomModel.getValueAt(selectedRow, 1).toString() + " " + showroomModel.getValueAt(selectedRow, 2).toString();

        String bookingQuery = "INSERT INTO bookings (plate_number) VALUES (?)";
        String vehicleUpdateQuery = "UPDATE vehicles SET status = 'Rented' WHERE plate_number = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false); 

            try (PreparedStatement bookingStmt = conn.prepareStatement(bookingQuery);
                 PreparedStatement vehicleStmt = conn.prepareStatement(vehicleUpdateQuery)) {

                bookingStmt.setString(1, plateNumber);
                bookingStmt.executeUpdate();

                vehicleStmt.setString(1, plateNumber);
                vehicleStmt.executeUpdate();

                conn.commit(); 
                JOptionPane.showMessageDialog(this, "Reservation Confirmed! Enjoy your " + brandAndModel, "Booking Success", JOptionPane.INFORMATION_MESSAGE);
                
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