package com.rental.entities;

import java.sql.Timestamp;

/**
 * Data entity class representing post-rental return logistics and asset recovery.
 * Maps directly to rows in the 'return_records' table.
 * * @author Sihath
 */
public class ReturnRecord {
    private int returnId;
    private int rentalId;
    private Timestamp returnDate;
    private int delayDays;
    private double lateFee;
    private String vehicleCondition; // e.g., "Good", "Damaged", "Excellent - Cleaned"

    // Full Constructor for extracting historical compliance logs from the DB
    public ReturnRecord(int returnId, int rentalId, Timestamp returnDate, int delayDays, double lateFee, String vehicleCondition) {
        this.returnId = returnId;
        this.rentalId = rentalId;
        this.returnDate = returnDate;
        this.delayDays = delayDays;
        this.lateFee = lateFee;
        this.vehicleCondition = vehicleCondition;
    }

    // Overloaded Constructor for generating fresh return logs within the Admin UI
    public ReturnRecord(int rentalId, int delayDays, double lateFee, String vehicleCondition) {
        this.rentalId = rentalId;
        this.delayDays = delayDays;
        this.lateFee = lateFee;
        this.vehicleCondition = vehicleCondition;
        this.returnDate = new Timestamp(System.currentTimeMillis()); // Captured instantly on submission
    }

    // ============================================================
    // GETTERS AND SETTERS (Encapsulation Principles)
    // ============================================================

    public int getReturnId() { return returnId; }
    public void setReturnId(int returnId) { this.returnId = returnId; }

    public int getRentalId() { return rentalId; }
    public void setRentalId(int rentalId) { this.rentalId = rentalId; }

    public Timestamp getReturnDate() { return returnDate; }
    public void setReturnDate(Timestamp returnDate) { this.returnDate = returnDate; }

    public int getDelayDays() { return delayDays; }
    public void setDelayDays(int delayDays) { this.delayDays = delayDays; }

    public double getLateFee() { return lateFee; }
    public void setLateFee(double lateFee) { this.lateFee = lateFee; }

    public String getVehicleCondition() { return vehicleCondition; }
    public void setVehicleCondition(String vehicleCondition) { this.vehicleCondition = vehicleCondition; }
}