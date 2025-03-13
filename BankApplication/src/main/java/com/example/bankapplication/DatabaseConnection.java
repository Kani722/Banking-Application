package com.example.bankapplication; // Add this at the top

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/my_database";
    private static final String USER = "root"; // Change if different
    private static final String PASSWORD = "ishan123"; // Enter your MySQL password

    public static Connection connect() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void createLoansTable() {
    }
}
