package com.example.bankapplication_admin;

import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LeaveRequestServer {
    private static final int SERVER_PORT = 5000;
    private static final CopyOnWriteArrayList<PrintWriter> clientWriters = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        System.out.println("Starting Leave Request Server...");

        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            System.out.println("Server started on port " + SERVER_PORT);

            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Accepeted New client connected: " + clientSocket.getInetAddress());

                    PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
                    clientWriters.add(writer);

                    new Thread(new ClientHandler(clientSocket, writer)).start();
                } catch (IOException e) {
                    System.err.println("[ERROR] Failed to accept client: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("[ERROR] Server failed to start: " + e.getMessage());
        }
    }

    /**
     * Sends a message to all connected clients.
     */
    public static void sendDecisionToClient(String message) {
        if (clientWriters.isEmpty()) {
            System.err.println("[ERROR] No active clients to send the message.");
            return;
        }

        for (PrintWriter writer : clientWriters) {
            try {
                writer.println(message);
                writer.flush();
                System.out.println("[INFO] Message sent to client: " + message);

            } catch (Exception e) {
                System.err.println("[ERROR] Failed to send message: " + e.getMessage());
                clientWriters.remove(writer);
            }
        }
        storeMessageInDatabase(message);
    }
    private static void storeMessageInDatabase(String message) {
        // Regular expression to extract Reason, Time, and Date
        Pattern pattern = Pattern.compile("Reason: (.*?), Time: (.*?), Date: (.*?)$");
        Matcher matcher = pattern.matcher(message);

        if (matcher.find()) {
            String reason = matcher.group(1).trim();
            String time = matcher.group(2).trim();
            String date = matcher.group(3).trim();

            try (Connection conn = DatabaseConnection.connect();
                 PreparedStatement stmt = conn.prepareStatement("INSERT INTO leave_requests (reason, time, date) VALUES (?, ?, ?)")) {

                stmt.setString(1, reason);
                stmt.setString(2, time);
                stmt.setString(3, date);

                int rowsInserted = stmt.executeUpdate();
                if (rowsInserted > 0) {
                    System.out.println("[INFO] Leave request successfully stored in the database.");
                }
            } catch (SQLException e) {
                System.err.println("[ERROR] Failed to store leave request: " + e.getMessage());
            }
        } else {
            System.err.println("[ERROR] Failed to extract leave request details from message.");
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private final PrintWriter writer;

        public ClientHandler(Socket clientSocket, PrintWriter writer) {
            this.clientSocket = clientSocket;
            this.writer = writer;
        }

        @Override
        public void run() {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                String clientMessage;
                while ((clientMessage = reader.readLine()) != null) {
                    System.out.println("[CLIENT] " + clientMessage);
                    sendDecisionToClient(clientMessage.toString());
                }
            } catch (IOException e) {
                System.out.println("[INFO] Client disconnected: " + clientSocket.getInetAddress());
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    System.err.println("[ERROR] Failed to close client socket: " + e.getMessage());
                }
                clientWriters.remove(writer);
            }
        }
    }
}
