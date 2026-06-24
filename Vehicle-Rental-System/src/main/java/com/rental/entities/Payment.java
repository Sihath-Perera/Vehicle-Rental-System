package com.rental.entities;

import java.sql.Timestamp;

/**
 * Data entity class representing financial transactions and invoices.
 * Maps directly to the 'payments' table rows in the database.
 * * @author Sihath
 */
public class Payment {
    private int paymentId;
    private int rentalId;
    private double amount;
    private Timestamp paymentDate;
    private String paymentMethod; // "Cash", "Card", "Online"
    private String status;         // "Paid", "Refunded"

    // Full Constructor for pulling historical records out of the database
    public Payment(int paymentId, int rentalId, double amount, Timestamp paymentDate, String paymentMethod, String status) {
        this.paymentId = paymentId;
        this.rentalId = rentalId;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
        this.status = status;
    }

    // Overloaded Constructor for creating fresh payments locally before saving to the DB
    public Payment(int rentalId, double amount, String paymentMethod) {
        this.rentalId = rentalId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = "Paid"; // Default state for a newly generated transaction
        this.paymentDate = new Timestamp(System.currentTimeMillis());
    }

    // ============================================================
    // GETTERS AND SETTERS (Encapsulation Principles)
    // ============================================================

    public int getPaymentId() { return paymentId; }
    public void setPaymentId(int paymentId) { this.paymentId = paymentId; }

    public int getRentalId() { return rentalId; }
    public void setRentalId(int rentalId) { this.rentalId = rentalId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public Timestamp getPaymentDate() { return paymentDate; }
    public void setPaymentDate(Timestamp paymentDate) { this.paymentDate = paymentDate; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}