package com.rental.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // Static instance variable to store the single connection manager
    private static DatabaseConnection instance;
    private Connection connection;
    
    private final String url = "jdbc:mysql://localhost:3306/vehicle_rental_db";
    private final String dbUser = "root";  
    private final String dbPass = "root";     

    // Private constructor enforces the Singleton Pattern
    private DatabaseConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(url, dbUser, dbPass);
            System.out.println(">>> Database Connected Successfully! <<<");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL Driver Missing: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Database Connection Failed: " + e.getMessage());
        }
    }

    // Global access point to get the single active database connector
    public static DatabaseConnection getInstance() throws SQLException {
        if (instance == null || instance.getConnection().isClosed()) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}