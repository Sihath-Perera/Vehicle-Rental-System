package com.rental.config; 

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    // Pre-configured cloud database credentials
    private static final String URL = "jdbc:mysql://b1hmut8htuasi2tb8u9s-mysql.services.clever-cloud.com:3306/b1hmut8htuasi2tb8u9s";
    private static final String USER = "umx3w2pvdds3gjvs";
    private static final String PASSWORD = "D5gcDOdgfKybvck7uplD";

    /**
     * Establishes a live connection to the Clever Cloud MySQL instance
     * @return Connection object
     */
    public static Connection getConnection() throws SQLException {
        try {
            // Force load the modern MySQL JDBC Driver we just repaired in pom.xml
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver missing! Check pom.xml setup.", e);
        }
    }

    // Isolated test mechanism to instantly verify database pipeline integrity
    public static void main(String[] args) {
        System.out.println("Testing Clever Cloud database pipeline connection...");
        try (Connection conn = getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("=========================================");
                System.out.println("✅ SUCCESS: Successfully linked to Clever Cloud!");
                System.out.println("=========================================");
            }
        } catch (SQLException e) {
            System.out.println("=========================================");
            System.out.println("❌ CONNECTION FAILED!");
            System.out.println("Error: " + e.getMessage());
            System.out.println("=========================================");
        }
    }
}