package com.rental.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;
    
    // Exact credentials from your Clever Cloud dashboard screenshot
    private final String host = "b1hmut8htuasi2tb8u9s-mysql.services.clever-cloud.com"; 
    private final String dbName = "b1hmut8htuasi2tb8u9s"; 
    private final String dbUser = "umx3w2pvdds3gjvs";  
    private final String dbPass = "D5gcDOdgfKybvck7uplD";      

    private DatabaseConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Appending time zone and SSL parameters for cloud compatibility
            String url = "jdbc:mysql://" + host + ":3306/" + dbName + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
            
            this.connection = DriverManager.getConnection(url, dbUser, dbPass);
            System.out.println(">>> Connected to Centralized Cloud Database! <<<");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL Driver Missing: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Cloud Database Connection Failed: " + e.getMessage());
        }
    }

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