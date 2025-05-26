package com.example.demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class VulnerableApp {

    // SonarLint will flag this as a Hardcoded Credential (java:S2068)
    private static final String DATABASE_PASSWORD = "MySecretPassword123!";

    public static void main(String[] args) {
        String userId = "admin'; DROP TABLE users;--"; // Malicious input for demo
        fetchUser(userId);
    }

    public static void fetchUser(String userId) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            // Establish a dummy connection (no actual DB needed for SonarLint to flag)
            conn = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", DATABASE_PASSWORD); // Using the hardcoded password

            // SonarLint will flag this as a SQL Injection (java:S5147)
            String sqlQuery = "SELECT * FROM users WHERE id = '" + userId + "'";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sqlQuery);

            System.out.println("Query executed: " + sqlQuery);
            if (rs.next()) {
                System.out.println("User found: " + rs.getString("name"));
            } else {
                System.out.println("No user found with ID: " + userId);
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        } finally {
            // Close resources in a finally block (good practice, but SonarLint checks this too)
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }
}