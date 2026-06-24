/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.rental.entities;

/**
 * Core Data Entity encapsulating vehicle attributes, daily rates, and operational states.
 * Maps directly to rows in the 'vehicles' table.
 * * @author Sihath
 */
public class Vehicle {
    private String plateNumber;
    private String brand;
    private String model;
    private double dailyRate;
    private String status; // "Available", "Rented", "Maintenance"

    public Vehicle(String plateNumber, String brand, String model, double dailyRate, String status) {
        this.plateNumber = plateNumber;
        this.brand = brand;
        this.model = model;
        this.dailyRate = dailyRate;
        this.status = status;
    }

    // ============================================================
    // GETTERS AND SETTERS (Encapsulation Principles)
    // ============================================================

    public String getPlateNumber() { return plateNumber; }
    public void setPlateNumber(String plateNumber) { this.plateNumber = plateNumber; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public double getDailyRate() { return dailyRate; }
    public void setDailyRate(double dailyRate) { this.dailyRate = dailyRate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}