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

    private JTable tblAvailableVehicles;
    private DefaultTableModel tableModel;
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
        } catch (Exception e) {
            // Fallback safely to default
        }

        initComponents();
        loadAvailableVehicles(); // ⚡ Live fetch available fleet from Cloud DB on startup!
    }

    private void initComponents() {
        setTitle("Customer Portal - Rent Your Vehicle");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setResizable(false);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(new Color(244, 246, 249));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        this.setContentPane(mainPanel);

        // ==========================================
        // 1. TOP CONTROL BANNER
        // ==========================================
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(30, 58, 138)); // Trustworthy Royal Corporate Blue
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("Available Rental Showroom", JLabel.LEFT);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
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
        // 2. CENTER PANEL: THE VEHICLE GRID SHOWCASE
        // ==========================================
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 233, 238), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblTableTitle = new JLabel("Select a vehicle from our active fleet below:", JLabel.LEFT);
        lblTableTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTableTitle.setForeground(new Color(51, 65, 85));
        centerPanel.add(lblTableTitle, BorderLayout.NORTH);

        String[] columns = {"Plate Number", "Brand", "Model", "Daily Rate (LKR)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };

        tblAvailableVehicles = new JTable(tableModel);
        tblAvailableVehicles.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblAvailableVehicles.setRowHeight(30);
        tblAvailableVehicles.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblAvailableVehicles.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // 🔄 MEMORY ANCHOR RETRIEVED: Old hardcoded rows completely removed!
        
        JScrollPane tableScrollPane = new JScrollPane(tblAvailableVehicles);
        centerPanel.add(tableScrollPane, BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // ==========================================
        // 3. SOUTH PANEL: BOOKING CTA ACTION ENGINE
        // ==========================================
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        southPanel.setBackground(new Color(244, 246, 249));

        btnBookVehicle = new JButton("Confirm Booking Reservation");
        btnBookVehicle.setPreferredSize(new Dimension(240, 42));
        btnBookVehicle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnBookVehicle.setBackground(new Color(22, 163, 74)); // Success Green
        btnBookVehicle.setForeground(Color.WHITE);
        btnBookVehicle.setFocusPainted(false);
        btnBookVehicle.setCursor(new Cursor(Cursor.HAND_CURSOR));
        southPanel.add(btnBookVehicle);

        mainPanel.add(southPanel, BorderLayout.SOUTH);

        // ==========================================
        // ⚡ BACKEND ACTION LISTENERS
        // ==========================================
        btnBookVehicle.addActionListener(e -> performVehicleBooking());
        btnLogout.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            this.dispose();
        });
    }

    /**
     * Dynamically filters and pulls only "Available" vehicles from Clever Cloud MySQL
     */
    private void loadAvailableVehicles() {
        tableModel.setRowCount(0); // Wipe stale data views
        String query = "SELECT * FROM vehicles WHERE status = 'Available'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                tableModel.addRow(new Object[]{
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

    /**
     * Books a vehicle, updates state to 'Rented', logs transaction to history
     */
    private void performVehicleBooking() {
        int selectedRow = tblAvailableVehicles.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please choose an available vehicle from the catalogue listing grid.", "No Car Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String plateNumber = tableModel.getValueAt(selectedRow, 0).toString();
        String brandAndModel = tableModel.getValueAt(selectedRow, 1).toString() + " " + tableModel.getValueAt(selectedRow, 2).toString();

        // Transaction handling: Log booking and update status simultaneously
        String bookingQuery = "INSERT INTO bookings (plate_number) VALUES (?)";
        String vehicleUpdateQuery = "UPDATE vehicles SET status = 'Rented' WHERE plate_number = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false); // Enable safe transactional state bounds

            try (PreparedStatement bookingStmt = conn.prepareStatement(bookingQuery);
                 PreparedStatement vehicleStmt = conn.prepareStatement(vehicleUpdateQuery)) {

                // 1. Log transaction entry
                bookingStmt.setString(1, plateNumber);
                bookingStmt.executeUpdate();

                // 2. Change vehicle status so it drops from public showroom catalog
                vehicleStmt.setString(1, plateNumber);
                vehicleStmt.executeUpdate();

                conn.commit(); // Push execution batch together safely
                JOptionPane.showMessageDialog(this, "Reservation Confirmed!\nYou have successfully booked the " + brandAndModel + " (" + plateNumber + ").", "Booking Success", JOptionPane.INFORMATION_MESSAGE);
                
                loadAvailableVehicles(); // Reload dynamic catalog list view immediately

            } catch (SQLException ex) {
                conn.rollback(); // Undo operations if any segment encounters failure conditions
                throw ex;
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Transaction Processing Interrupted: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new CustomerPortal().setVisible(true));
    }
}