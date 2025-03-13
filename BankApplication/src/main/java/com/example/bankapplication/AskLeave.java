package com.example.bankapplication;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AskLeave {

    @FXML
    private TextArea leaveReasonTextArea;

    @FXML
    private TextField leaveTimeTextField;

    @FXML
    private DatePicker leaveDatePicker;

    @FXML
    private Button backButton;

    private final String SERVER_IP = "192.168.8.172"; // Change this to Admin PC's IP
    private final int SERVER_PORT = 5000; // Must match server's port

    // Method to handle form submission
    @FXML
    private void submitLeaveRequest() {
        String reason = leaveReasonTextArea.getText();
        String time = leaveTimeTextField.getText();
        String date = (leaveDatePicker.getValue() != null) ? leaveDatePicker.getValue().toString() : "No date selected";

        if (reason.isEmpty() || time.isEmpty() || date.equals("No date selected")) {
            showAlert("Error", "All fields must be filled out.", AlertType.ERROR);
            System.out.println("Error: Leave request form is incomplete.");
        } else {
            String leaveData = "Reason: " + reason + ", Time: " + time + ", Date: " + date ;
            sendLeaveRequest(leaveData);
            saveLeaveRequestToDatabase(reason, time, date); // Save to database
        }
    }

    // Method to send leave request over a socket (Optional, if needed)
    private void sendLeaveRequest(String leaveData) {
        System.out.println("Connecting to server at " + SERVER_IP + ":" + SERVER_PORT);
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            out.println(leaveData);
            showAlert("Success", "Your leave request has been sent to the admin.", AlertType.INFORMATION);
            System.out.println("Leave request sent successfully: " + leaveData);

        } catch (IOException e) {
            showAlert("Error", "Failed to send leave request.", AlertType.ERROR);
            System.err.println("Failed to send leave request. Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to save leave request to the database
    private void saveLeaveRequestToDatabase(String reason, String time, String date) {
        String insertQuery = "INSERT INTO leave_requests (reason, time, date) VALUES (?, ?, ?)";

        // Using the DatabaseConnection class to connect to the database
        try (Connection connection = DatabaseConnection.connect();  // Get connection
             PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {

            if (connection != null) {
                preparedStatement.setString(1, reason);
                preparedStatement.setString(2, time);
                preparedStatement.setString(3, date);

                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Leave request saved to database.");
                } else {
                    System.out.println("Failed to save leave request to database.");
                }
            } else {
                System.out.println("Database connection failed.");
            }

        } catch (SQLException e) {
            showAlert("Error", "Failed to save leave request to the database.", AlertType.ERROR);
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to display alerts
    private void showAlert(String title, String message, AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleBackButton() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("dashboard-view.fxml"));
            Scene dashboardScene = new Scene(fxmlLoader.load(), 800, 500);

            Stage stage = (Stage) backButton.getScene().getWindow(); // Get current stage
            stage.setScene(dashboardScene);
            stage.setTitle("Dashboard");
            System.out.println("Navigating back to Dashboard.");

        } catch (IOException e) {
            System.err.println("Error loading Dashboard view: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
