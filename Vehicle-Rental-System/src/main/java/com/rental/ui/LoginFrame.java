package com.rental.ui;

import com.rental.config.DatabaseConnection;
import com.rental.controller.RentalController;
import com.rental.entities.User;
import com.rental.entities.Vehicle;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class AdminDashboard extends JFrame {
    private User adminUser;
    private RentalController controller;

    // UI Components
    private JTabbedPane tabbedPane;
    private DefaultTableModel vehicleModel, pendingModel, paymentModel, activeRentalsModel;
    private JTable vehicleTable, pendingTable, paymentTable, activeRentalsTable;
    
    // Input Fields
    private JTextField txtPlate, txtBrand, txtModel, txtRate;
    private JTextField txtReturnRentalId, txtReturnPlate, txtDelayDays, txtLateFee;
    private JComboBox<String> cmbCondition;
    private JComboBox<String> cmbPaymentMethod; // Added payment method selection

    public AdminDashboard(User adminUser) {
        this.adminUser = adminUser;
        this.controller = new RentalController();
        initializeUI();
        refreshAllData();
    }

    private void initializeUI() {
        setTitle("DriveFlow Management System - Admin: " + adminUser.getUsername());
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header with Logout
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(15, 23, 42)); // Dark modern slate
        header.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Padding
        
        JLabel lblTitle = new JLabel("DriveFlow Corporate ERP", SwingConstants.LEFT);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);
        header.add(lblTitle, BorderLayout.WEST);
        
        // Logout Button
        JButton btnLogout = new JButton("Logout");
        btnLogout.setBackground(new Color(220, 38, 38)); // Danger Red
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.setFocusPainted(false);
        btnLogout.addActionListener(e -> {
            this.dispose();
            new LoginFrame().setVisible(true);
        });
        header.add(btnLogout, BorderLayout.EAST);
        
        add(header, BorderLayout.NORTH);

        // Core Layout
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        setupFleetTab();
        setupPendingTab();
        setupReturnsTab();
        setupFinancialsTab();
        add(tabbedPane, BorderLayout.CENTER);
    }

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
        
        JPanel formPanel = new JPanel(new FlowLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        txtPlate = new JTextField(8); txtBrand = new JTextField(8);
        txtModel = new JTextField(8); txtRate = new JTextField(8);
        JButton btnAdd = new JButton("Add Vehicle");
        
        formPanel.add(new JLabel("Plate:")); formPanel.add(txtPlate);
        formPanel.add(new JLabel("Brand:")); formPanel.add(txtBrand);
        formPanel.add(new JLabel("Model:")); formPanel.add(txtModel);
        formPanel.add(new JLabel("Rate:")); formPanel.add(txtRate);
        formPanel.add(btnAdd);
        
        vehicleModel = new DefaultTableModel(new String[]{"Plate", "Brand", "Model", "Rate", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        vehicleTable = new JTable(vehicleModel);
        applyModernTableStyle(vehicleTable);
        
        btnAdd.addActionListener(e -> {
            try {
                if(controller.addNewVehicle(txtPlate.getText(), txtBrand.getText(), txtModel.getText(), Double.parseDouble(txtRate.getText()))){
                    JOptionPane.showMessageDialog(this, "Vehicle added!");
                    refreshAllData();
                }
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
        });

        panel.add(formPanel, BorderLayout.SOUTH);
        panel.add(new JScrollPane(vehicleTable), BorderLayout.CENTER);
        tabbedPane.addTab("Fleet Inventory", panel);
    }

    private void setupPendingTab() {
        JPanel panel = new JPanel(new BorderLayout());
        
        pendingModel = new DefaultTableModel(new String[]{"Rental ID", "Customer", "Plate", "Cost", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        pendingTable = new JTable(pendingModel);
        applyModernTableStyle(pendingTable);
        
        JButton btnApprove = new JButton("Approve Rental");
        btnApprove.setBackground(new Color(37, 99, 235));
        btnApprove.setForeground(Color.WHITE);
        
        btnApprove.addActionListener(e -> {
            int row = pendingTable.getSelectedRow();
            if(row != -1) {
                try {
                    controller.approveRental((int)pendingModel.getValueAt(row, 0), (String)pendingModel.getValueAt(row, 2));
                    refreshAllData();
                } catch (SQLException ex) { JOptionPane.showMessageDialog(this, "Err: " + ex.getMessage()); }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a pending rental to approve.");
            }
        });
        
        panel.add(new JScrollPane(pendingTable), BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(btnApprove);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        tabbedPane.addTab("Pending Approvals", panel);
    }

    private void setupReturnsTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        activeRentalsModel = new DefaultTableModel(new String[]{"Rental ID", "Plate", "End Date"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        activeRentalsTable = new JTable(activeRentalsModel);
        applyModernTableStyle(activeRentalsTable);
        
        activeRentalsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && activeRentalsTable.getSelectedRow() != -1) {
                int row = activeRentalsTable.getSelectedRow();
                txtReturnRentalId.setText(activeRentalsModel.getValueAt(row, 0).toString());
                txtReturnPlate.setText(activeRentalsModel.getValueAt(row, 1).toString());
                txtDelayDays.setText("0");
                txtLateFee.setText("0.0");
            }
        });

        // Configured to 7 grid lines layout rows for the new payment option field
        JPanel form = new JPanel(new GridLayout(7, 2, 10, 10));
        form.setBorder(BorderFactory.createTitledBorder("Return Details"));
        
        txtReturnRentalId = new JTextField(); 
        txtReturnRentalId.setEditable(false); 
        txtReturnPlate = new JTextField(); 
        txtReturnPlate.setEditable(false);    
        
        txtDelayDays = new JTextField("0"); 
        txtLateFee = new JTextField("0.0");
        
        txtDelayDays.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateFee(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateFee(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateFee(); }

            private void updateFee() {
                Runnable doUpdate = () -> {
                    try {
                        String text = txtDelayDays.getText().trim();
                        if (text.isEmpty() || text.equals("-")) {
                            txtLateFee.setText("0.0");
                            return;
                        }
                        int days = Integer.parseInt(text);
                        double penaltyPerDay = 500.0; // Flat base parameter allocation
                        double totalFee = Math.max(0, days * penaltyPerDay); 
                        txtLateFee.setText(String.format("%.1f", totalFee));
                    } catch (NumberFormatException ex) {
                        txtLateFee.setText("0.0");
                    }
                };
                SwingUtilities.invokeLater(doUpdate);
            }
        });

        cmbCondition = new JComboBox<>(new String[]{"Excellent", "Good", "Damaged"});
        cmbPaymentMethod = new JComboBox<>(new String[]{"Cash", "Card", "Bank Transfer"}); // Instantiation
        
        JButton btnReturn = new JButton("Process Return");
        btnReturn.setBackground(new Color(16, 185, 129));
        btnReturn.setForeground(Color.WHITE);
        
        form.add(new JLabel("Rental ID:")); form.add(txtReturnRentalId);
        form.add(new JLabel("Plate:")); form.add(txtReturnPlate);
        form.add(new JLabel("Delay Days:")); form.add(txtDelayDays);
        form.add(new JLabel("Late Fee:")); form.add(txtLateFee);
        form.add(new JLabel("Condition:")); form.add(cmbCondition);
        form.add(new JLabel("Payment Method:")); form.add(cmbPaymentMethod); // UI Attachment
        form.add(new JLabel("")); form.add(btnReturn);
        
        btnReturn.addActionListener(e -> {
            if (txtReturnRentalId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select an active rental from the table on the left.");
                return;
            }
            try {
                // Expanded call execution sequence block targeting updated schema definition parameters
                controller.processReturn(
                        Integer.parseInt(txtReturnRentalId.getText()), 
                        txtReturnPlate.getText(), 
                        Integer.parseInt(txtDelayDays.getText()), 
                        Double.parseDouble(txtLateFee.getText()), 
                        (String)cmbCondition.getSelectedItem(),
                        (String)cmbPaymentMethod.getSelectedItem()
                );
                
                JOptionPane.showMessageDialog(this, "Return Processed and Financial Ledger Generated Successfully!");
                
                txtReturnRentalId.setText(""); txtReturnPlate.setText("");
                txtDelayDays.setText("0"); txtLateFee.setText("0.0");
                
                refreshAllData();
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Err: " + ex.getMessage()); }
        });
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(activeRentalsTable), form);
        splitPane.setDividerLocation(450);
        
        panel.add(splitPane, BorderLayout.CENTER);
        tabbedPane.addTab("Process Returns", panel);
    }

    private void setupFinancialsTab() {
        paymentModel = new DefaultTableModel(new String[]{"Payment ID", "Rental ID", "Amount", "Method"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        paymentTable = new JTable(paymentModel);
        applyModernTableStyle(paymentTable);
        tabbedPane.addTab("Revenue Ledger", new JScrollPane(paymentTable));
    }

    private void refreshAllData() {
        vehicleModel.setRowCount(0);
        pendingModel.setRowCount(0);
        paymentModel.setRowCount(0);
        if (activeRentalsModel != null) {
            activeRentalsModel.setRowCount(0);
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Load Fleet
            for (Vehicle v : controller.getVehiclesByStatus("Available")) {
                vehicleModel.addRow(new Object[]{v.getPlateNumber(), v.getBrand(), v.getModel(), v.getDailyRate(), v.getStatus()});
            }

            // Load Pending
            String pendingQuery = "SELECT r.rental_id, u.username, r.vehicle_plate, r.total_cost, r.status " +
                                  "FROM rental_records r JOIN users u ON r.customer_id = u.user_id " +
                                  "WHERE r.status = 'Pending'";
            try (PreparedStatement stmt = conn.prepareStatement(pendingQuery);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    pendingModel.addRow(new Object[]{
                        rs.getInt("rental_id"), 
                        rs.getString("username"), 
                        rs.getString("vehicle_plate"), 
                        rs.getDouble("total_cost"), 
                        rs.getString("status")
                    });
                }
            }

            // Load Active
            String activeQuery = "SELECT rental_id, vehicle_plate, end_date FROM rental_records WHERE status = 'Active'";
            try (PreparedStatement stmt = conn.prepareStatement(activeQuery);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    activeRentalsModel.addRow(new Object[]{
                        rs.getInt("rental_id"), 
                        rs.getString("vehicle_plate"), 
                        rs.getDate("end_date")
                    });
                }
            }

            // Load Revenue Ledger Elements
            String revenueQuery = "SELECT payment_id, rental_id, amount, payment_method FROM payments";
            try (PreparedStatement stmt = conn.prepareStatement(revenueQuery);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    paymentModel.addRow(new Object[]{
                        rs.getInt("payment_id"), 
                        rs.getInt("rental_id"), 
                        rs.getDouble("amount"), 
                        rs.getString("payment_method")
                    });
                }
            } catch (SQLException e) {
                System.err.println("Note: Payments table sync variant failure context verification.");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Sync Error: " + ex.getMessage());
        }
    }
    
    public static void main(String[] args) {
        User testAdmin = new User(1, "AdminTest", "password", "ADMIN") {};
        java.awt.EventQueue.invokeLater(() -> {
            new AdminDashboard(testAdmin).setVisible(true);
        });
    }
}