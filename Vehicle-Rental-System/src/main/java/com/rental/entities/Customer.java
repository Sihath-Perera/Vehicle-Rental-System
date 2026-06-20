/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.rental.entities;

public class Customer extends User {
    private String phoneNumber;
    private String licenseNumber;

    public Customer(int userId, String username, String password, String phoneNumber, String licenseNumber) {
        super(userId, username, password, "CUSTOMER");
        this.phoneNumber = phoneNumber;
        this.licenseNumber = licenseNumber;
    }

    // Getters and Setters
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }
}
