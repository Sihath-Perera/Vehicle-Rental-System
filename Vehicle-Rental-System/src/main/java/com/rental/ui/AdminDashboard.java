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
        loadVehicleData(); // ⚡ Live fetch from Cloud DB on startup!
    }

    private void initComponents() {
        setTitle("Admin Dashboard - Fleet Management Control");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 650);
        setResizable(false);
        setLocationRelativeTo(null); 

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(new Color(244, 246, 249));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        this.setContentPane(mainPanel);

        // ==========================================
        // 1. TOP CONTROL HEADER BANNER
        // ==========================================
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(15, 23, 42)); 
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

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

        String[] columns = {"Plate Number", "Brand", "Model", "Daily Rate (LKR)", "Current Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };

        tblVehicles = new JTable(tableModel);
        tblVehicles.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblVehicles.setRowHeight(28);
        tblVehicles.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblVehicles.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

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

        JSeparator formSep = new JSeparator();
        gbc.gridy = 11;
        gbc.insets = new Insets(12, 0, 12, 0);
        eastPanel.add(formSep, gbc);
        gbc.insets = new Insets(5, 0, 5, 0);

        // Action Buttons
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

        // ⚡ BACKEND ACTION LISTENERS
        btnAddVehicle.addActionListener(e -> performVehicleRegistration());
        btnUpdateStatus.addActionListener(e -> performStatusUpdate());
        btnLogout.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            this.dispose();
        });
        
        // Listen for table row selection to automatically fill form fields for easy status updates
        tblVehicles.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = tblVehicles.getSelectedRow();
            if (selectedRow != -1) {
                txtPlateNumber.setText(tableModel.getValueAt(selectedRow, 0).toString());
                txtBrand.setText(tableModel.getValueAt(selectedRow, 1).toString());
                txtModel.setText(tableModel.getValueAt(selectedRow, 2).toString());
                txtDailyRate.setText(tableModel.getValueAt(selectedRow, 3).toString());
                cmbStatus.setSelectedItem(tableModel.getValueAt(selectedRow, 4).toString());
            }
        });
    }

    /**
     * Pulls the latest fleet entries from Clever Cloud and populates JTable dynamically
     */
    private void loadVehicleData() {
        tableModel.setRowCount(0); // Clear old entries completely
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
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load vehicles: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Inserts a completely new vehicle into Clever Cloud MySQL
     */
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
            loadVehicleData(); // Refresh the grid view dynamically

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Registration failed: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Modifies the operational availability status of an existing vehicle
     */
    private void performStatusUpdate() {
        int selectedRow = tblVehicles.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a vehicle from the table grid first to update.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String plate = tableModel.getValueAt(selectedRow, 0).toString();
        String newStatus = cmbStatus.getSelectedItem().toString();

        String query = "UPDATE vehicles SET status = ? WHERE plate_number = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, newStatus);
            pstmt.setString(2, plate);

            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Operational status updated for vehicle: " + plate);
            
            clearFormFields();
            loadVehicleData(); // Reload fresh grid state

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Update failed: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
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