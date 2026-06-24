package com.rental.entities;

import java.sql.Date;

/**
 * Data entity class representing active agreements and vehicle reservations.
 * Maps directly to rows in the 'rental_records' table.
 * * @author Sihath
 */
public class RentalRecord {
    private int rentalId;
    private int customerId;
    private String vehiclePlate;
    private Date startDate;
    private Date endDate;
    private double totalCost;
    private String status; // "Pending", "Approved", "Rejected", "Active", "Completed"

    // Full Constructor for reading historical booking data directly from the DB
    public RentalRecord(int rentalId, int customerId, String vehiclePlate, Date startDate, Date endDate, double totalCost, String status) {
        this.rentalId = rentalId;
        this.customerId = customerId;
        this.vehiclePlate = vehiclePlate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalCost = totalCost;
        this.status = status;
    }

    // Overloaded Constructor for generating fresh bookings locally via CustomerPortal
    public RentalRecord(int customerId, String vehiclePlate, Date startDate, Date endDate, double totalCost) {
        this.customerId = customerId;
        this.vehiclePlate = vehiclePlate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalCost = totalCost;
        this.status = "Pending"; // All reservations default to Pending until Admin approval
    }

    // ============================================================
    // GETTERS AND SETTERS (Encapsulation Principles)
    // ============================================================

    public int getRentalId() { return rentalId; }
    public void setRentalId(int rentalId) { this.rentalId = rentalId; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getVehiclePlate() { return vehiclePlate; }
    public void setVehiclePlate(String vehiclePlate) { this.vehiclePlate = vehiclePlate; }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }

    public double getTotalCost() { return totalCost; }
    public void setTotalCost(double totalCost) { this.totalCost = totalCost; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}