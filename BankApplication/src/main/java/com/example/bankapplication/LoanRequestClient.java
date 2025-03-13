package com.example.bankapplication;

import java.io.*;
import java.net.*;
import java.sql.*;

public class LoanRequestClient {

    private static final String SERVER_ADDRESS = "192.168.8.172";  // Localhost for local server
    private static final int SERVER_PORT = 6000;  // Server port

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Connection conn = DatabaseConnection.connect()) {  // Use DatabaseConnection class

            if (conn == null) {
                System.err.println("[ERROR] Database connection failed.");
                return;
            }

            System.out.println("Connected to Loan Request Server. Waiting for messages...");

            String serverMessage;
            while ((serverMessage = reader.readLine()) != null) {
                System.out.println("[SERVER] " + serverMessage);
                saveLoanRequestToDatabase(conn, serverMessage);
            }

        } catch (IOException e) {
            System.err.println("[ERROR] Connection lost: " + e.getMessage());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void saveLoanRequestToDatabase(Connection conn, String message) {
        // Extract loan request details from the server message
        String[] parts = message.split(" - ");
        if (parts.length >= 2) {
            String details = parts[0].trim();  // Extract details like "Loan Amount: 10000, Term: 5 years"
            String response = parts[1].trim(); // Extract response like "Your loan request has been APPROVED"

            String sql = "INSERT INTO loan_requests (details, response, created_at) VALUES (?, ?, NOW())";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, details);
                stmt.setString(2, response);
                stmt.executeUpdate();
                System.out.println("[INFO] Loan request saved successfully.");
            } catch (SQLException e) {
                System.err.println("[ERROR] Failed to save loan request: " + e.getMessage());
            }
        } else {
            System.err.println("[ERROR] Invalid message format.");
        }
    }
}
