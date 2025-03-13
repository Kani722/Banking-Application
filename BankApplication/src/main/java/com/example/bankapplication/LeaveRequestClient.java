package com.example.bankapplication;

import java.io.*;
import java.net.*;
import java.sql.*;

public class LeaveRequestClient {
    private static final String SERVER_ADDRESS = "192.168.8.172";  // Change if needed
    private static final int SERVER_PORT = 5000;


    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Connection conn = DatabaseConnection.connect()) {  // Use DatabaseConnection class

            if (conn == null) {
                System.err.println("[ERROR] Database connection failed.");
                return;
            }

            System.out.println("Connected to Leave Request Server. Waiting for messages...");

            String serverMessage;
            while ((serverMessage = reader.readLine()) != null) {
                System.out.println("[SERVER] " + serverMessage);
                saveNotificationToDatabase(conn, serverMessage);
            }

        } catch (IOException e) {
            System.err.println("[ERROR] Connection lost: " + e.getMessage());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void saveNotificationToDatabase(Connection conn, String message) {
        // Extract the response message
        String[] parts = message.split(" - ");
        if (parts.length >= 2) {
            String details = parts[0].trim();  // Extract "Reason: re, Date: 2025-02-06, Time: ergerg"
            String response = parts[1].trim(); // Extract "Your request has been ACCEPTED"

            String sql = "INSERT INTO notifications (details, response, created_at) VALUES (?, ?, NOW())";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, details);
                stmt.setString(2, response);
                stmt.executeUpdate();
                System.out.println("[INFO] Notification saved successfully.");
            } catch (SQLException e) {
                System.err.println("[ERROR] Failed to save notification: " + e.getMessage());
            }
        } else {
            System.err.println("[ERROR] Invalid message format.");
        }
    }
}
