package com.rental.controller;

import com.rental.config.DatabaseConnection;
import com.rental.entities.Customer;
import com.rental.entities.User;
import com.rental.entities.Vehicle;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Central controller managing database interactions (CRUD) and transaction
 * logic between the UI presentation layer and the Clever Cloud MySQL instance.
 * @author Sihath
 */
public class RentalController {

    // ============================================================
    // 1. AUTHENTICATION & IDENTITY OPERATIONS
    // ============================================================

    public User login(String username, String password) throws SQLException {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, username);
            stmt.setString(2, password);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("user_id");
                    String role = rs.getString("role");
                    String phone = rs.getString("phone_number");
                    String license = rs.getString("license_number");
                    
                    if ("CUSTOMER".equalsIgnoreCase(role)) {
                        return new Customer(id, username, password, phone, license);
                    } else {
                        return new User(id, username, password, role) {};
                    }
                }
            }
        }
        return null;
    }

    public boolean registerCustomer(String username, String password, String fullName, 
                                    String address, String licenseNumber, String phoneNumber) throws SQLException {
        String query = "INSERT INTO users (username, password, role, full_name, address, license_number, phone_number) "
                     + "VALUES (?, ?, 'CUSTOMER', ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, fullName);
            stmt.setString(4, address);
            stmt.setString(5, licenseNumber);
            stmt.setString(6, phoneNumber);
            
            return stmt.executeUpdate() > 0;
        }
    }

    // ============================================================
    // 2. FLEET LOGISTICS OPERATIONS (Vehicles)
    // ============================================================

    public List<Vehicle> getVehiclesByStatus(String statusFilter) throws SQLException {
        List<Vehicle> vehicleList = new ArrayList<>();
        String query = "SELECT * FROM vehicles WHERE status = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, statusFilter);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Vehicle vehicle = new Vehicle(
                        rs.getString("plate_number"),
                        rs.getString("brand"),
                        rs.getString("model"),
                        rs.getDouble("daily_rate"),
                        rs.getString("status")
                    );
                    vehicleList.add(vehicle);
                }
            }
        }
        return vehicleList;
    }

    // ============================================================
    // 3. TRANSACTIONAL RENTAL & RETURN LOGISTICS
    // ============================================================

    public boolean createRentalRecord(int customerId, String vehiclePlate, Date startDate, 
                                      Date endDate, double totalCost) throws SQLException {
        String query = "INSERT INTO rental_records (customer_id, vehicle_plate, start_date, end_date, total_cost, status) "
                     + "VALUES (?, ?, ?, ?, ?, 'Pending')";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, customerId);
            stmt.setString(2, vehiclePlate);
            stmt.setDate(3, startDate);
            stmt.setDate(4, endDate);
            stmt.setDouble(5, totalCost);
            
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean approveRental(int rentalId, String vehiclePlate) throws SQLException {
        String updateRental = "UPDATE rental_records SET status = 'Active' WHERE rental_id = ?";
        String updateVehicle = "UPDATE vehicles SET status = 'Rented' WHERE plate_number = ?";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false); 
            
            try (PreparedStatement stmtRental = conn.prepareStatement(updateRental);
                 PreparedStatement stmtVehicle = conn.prepareStatement(updateVehicle)) {
                
                stmtRental.setInt(1, rentalId);
                stmtRental.executeUpdate();
                
                stmtVehicle.setString(1, vehiclePlate);
                stmtVehicle.executeUpdate();
                
                conn.commit(); 
                return true;
            } catch (SQLException ex) {
                conn.rollback(); 
                throw ex;
            }
        }
    }

    /**
     * Processes vehicle drop-off returns. Automatically queries the lease record context 
     * to capture base values, processes penalties, updates infrastructure allocation status tables, 
     * and logs the finalized transaction directly to the payment system ledger table.
     * * Parameter parameters expanded to integrate string sequence formatting matching paymentMethod allocations.
     */
    public boolean processReturn(int rentalId, String vehiclePlate, int delayDays, 
                                 double lateFee, String condition, String paymentMethod) throws SQLException {
        String insertReturn = "INSERT INTO return_records (rental_id, delay_days, late_fee, vehicle_condition) VALUES (?, ?, ?, ?)";
        String updateRental = "UPDATE rental_records SET status = 'Completed' WHERE rental_id = ?";
        String updateVehicle = "UPDATE vehicles SET status = 'Available' WHERE plate_number = ?";
        String getBaseCost = "SELECT total_cost FROM rental_records WHERE rental_id = ?";
        String insertPayment = "INSERT INTO payments (rental_id, amount, payment_method, status) VALUES (?, ?, ?, 'Paid')";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false); // Enable transactional safety integrity context
            
            try (PreparedStatement stmtReturn = conn.prepareStatement(insertReturn);
                 PreparedStatement stmtRental = conn.prepareStatement(updateRental);
                 PreparedStatement stmtVehicle = conn.prepareStatement(updateVehicle);
                 PreparedStatement stmtCost = conn.prepareStatement(getBaseCost);
                 PreparedStatement stmtPayment = conn.prepareStatement(insertPayment)) {
                
                // 1. Log return record structural constraints
                stmtReturn.setInt(1, rentalId);
                stmtReturn.setInt(2, delayDays);
                stmtReturn.setDouble(3, lateFee);
                stmtReturn.setString(4, condition);
                stmtReturn.executeUpdate();
                
                // 2. Set rental verification parameters to trace structural closeouts
                stmtRental.setInt(1, rentalId);
                stmtRental.executeUpdate();
                
                // 3. Mark vehicle properties back to free allocation spaces
                stmtVehicle.setString(1, vehiclePlate);
                stmtVehicle.executeUpdate();
                
                // 4. Retrieve primary financial obligations from baseline lease records
                double baseCost = 0.0;
                stmtCost.setInt(1, rentalId);
                try (ResultSet rs = stmtCost.executeQuery()) {
                    if (rs.next()) {
                        baseCost = rs.getDouble("total_cost");
                    }
                }
                
                // 5. Inject complete dynamic receipt parameters into the payment metrics engine
                double aggregatedRevenue = baseCost + lateFee;
                stmtPayment.setInt(1, rentalId);
                stmtPayment.setDouble(2, aggregatedRevenue);
                stmtPayment.setString(3, paymentMethod);
                stmtPayment.executeUpdate();
                
                conn.commit(); // Finalize batch data processing routines securely
                return true;
            } catch (SQLException ex) {
                conn.rollback(); // Revert trace vectors clean if constraints bounce
                throw ex;
            }
        }
    }

    // ============================================================
    // 4. FINANCIAL PAYMENT OPERATIONS
    // ============================================================

    public boolean RecordPayment(int rentalId, double amount, String paymentMethod) throws SQLException {
        String query = "INSERT INTO payments (rental_id, amount, payment_method, status) VALUES (?, ?, ?, 'Paid')";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, rentalId);
            stmt.setDouble(2, amount);
            stmt.setString(3, paymentMethod);
            
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean addNewVehicle(String plate, String brand, String model, double dailyRate) throws SQLException {
        String query = "INSERT INTO vehicles (plate_number, brand, model, daily_rate, status) VALUES (?, ?, ?, ?, 'Available')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, plate);
            stmt.setString(2, brand);
            stmt.setString(3, model);
            stmt.setDouble(4, dailyRate);
            return stmt.executeUpdate() > 0;
        }
    }
}