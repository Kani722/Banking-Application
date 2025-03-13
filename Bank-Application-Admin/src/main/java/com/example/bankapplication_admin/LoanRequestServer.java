package com.example.bankapplication_admin;

import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoanRequestServer {
    private static final int SERVER_PORT = 6000;
    private static final CopyOnWriteArrayList<PrintWriter> clientWriters = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        System.out.println("Starting Loan Request Server...");

        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            System.out.println("Server started on port " + SERVER_PORT);

            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("New client connected: " + clientSocket.getInetAddress());

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
     * Broadcasts a message to all connected clients.
     */
    public static void broadcastMessage(String message) {
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
    }

    /**
     * Stores loan request in the database.
     */
    private static void storeMessageInDatabase(String message) {
        Pattern pattern = Pattern.compile(
                "Loan ID: (\\d+), Type: (.*?), Requester: (.*?), Email: (.*?), Contact: (\\d+), Price: ([\\d.]+), Tax: ([\\d.]+), Interest: ([\\d.]+)"
        );
        Matcher matcher = pattern.matcher(message);

        if (matcher.find()) {
            int loanId = Integer.parseInt(matcher.group(1));
            String type = matcher.group(2).trim();
            String requester = matcher.group(3).trim();
            String email = matcher.group(4).trim();
            String contact = matcher.group(5).trim();
            double price = Double.parseDouble(matcher.group(6));
            double tax = Double.parseDouble(matcher.group(7));
            double interest = Double.parseDouble(matcher.group(8));

            try (Connection conn = DatabaseConnection.connect();
                 PreparedStatement stmt = conn.prepareStatement(
                         "INSERT INTO loan_requests (loan_id, type, requester, email, contact, price, tax, interest) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
                 )) {

                stmt.setInt(1, loanId);
                stmt.setString(2, type);
                stmt.setString(3, requester);
                stmt.setString(4, email);
                stmt.setString(5, contact);
                stmt.setDouble(6, price);
                stmt.setDouble(7, tax);
                stmt.setDouble(8, interest);

                int rowsInserted = stmt.executeUpdate();
                if (rowsInserted > 0) {
                    System.out.println("[INFO] Loan request successfully stored in the database.");
                }
            } catch (SQLException e) {
                System.err.println("[ERROR] Failed to store loan request: " + e.getMessage());
            }
        } else {
            System.err.println("[ERROR] Failed to extract loan request details from message.");
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
                    // Display received message from client
                    System.out.println("[CLIENT MESSAGE] " + clientMessage);

                    // Store message in the database
                    storeMessageInDatabase(clientMessage);

                    // Forward message to all clients (acting as a notification system)
                    broadcastMessage("[New Loan Request] " + clientMessage);
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
