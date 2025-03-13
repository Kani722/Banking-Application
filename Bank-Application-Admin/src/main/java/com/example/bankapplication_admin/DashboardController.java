package com.example.bankapplication_admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML
    private TableView<LeaveRequest> leaveRequestTable;
    @FXML
    private TextField usernameField;
    @FXML
    private TableColumn<LeaveRequest, String> reasonColumn;
    @FXML
    private ImageView bankImageView;

    @FXML
    private TableColumn<LeaveRequest, String> timeColumn;

    @FXML
    private TableColumn<LeaveRequest, String> dateColumn;

    @FXML
    private TableColumn<LeaveRequest, Void> actionColumn;

    private final ObservableList<LeaveRequest> leaveRequests = FXCollections.observableArrayList();
    private PrintWriter serverWriter;
    @FXML
    private void handleAcceptLoans() {
        // Code to handle loan request acceptance
        System.out.println("Accept Loans button clicked!");
        // Add logic to handle loan request acceptance (e.g., update status in database)
    }

    @FXML
    private void handleAcceptLeaves() {
        // Code to handle leave request acceptance
        System.out.println("Accept Leaves button clicked!");
        loadLeaveRequests();
        // You can add logic here to accept the leave requests or show additional details
    }
    @FXML
    private void acccpts(ActionEvent event) {
        try {
            // Ensure that the file path is correct
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("LoanRequestsPage.fxml"));

            // Load the FXML file and set it into the scene
            Parent root = fxmlLoader.load();
            Scene acceptScreen = new Scene(root, 800, 500);

            // Get current stage and set the new scene
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(acceptScreen);
            stage.setTitle("Loan Requests");

            System.out.println("Loan Requests Page Opened!");

        } catch (IOException e) {
            System.err.println("Error loading LoanRequestsPage.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }






    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        reasonColumn.setCellValueFactory(new PropertyValueFactory<>("reason"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button acceptButton = new Button("Accept");
            private final Button rejectButton = new Button("Reject");
            private final HBox buttonsBox = new HBox(10, acceptButton, rejectButton);

            {
                acceptButton.setOnAction(event -> handleDecision(getIndex(), true));
                rejectButton.setOnAction(event -> handleDecision(getIndex(), false));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttonsBox);
                }
            }
        });

        connectToServer();
        loadLeaveRequests();
        leaveRequestTable.setItems(leaveRequests);

        try {
            // Load image from "resources/images/" folder
            URL imageUrl = getClass().getResource("/images/loginimage.png");

            if (imageUrl != null) {
                Image bankImage = new Image(imageUrl.toString());
                bankImageView.setImage(bankImage);
            } else {
                System.out.println("Image not found in resources.");
            }
        } catch (Exception e) {
            System.out.println("Error loading image: " + e.getMessage());
        }
    }

    /**
     * Establishes a connection to the LeaveRequestServer.
     */
    private void connectToServer() {
        try {
            Socket serverSocket = new Socket("localhost", 5000);
            serverWriter = new PrintWriter(serverSocket.getOutputStream(), true);
            System.out.println("[INFO] Connected to Leave Request Server.");
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to connect to server: " + e.getMessage());
        }
    }

    /**
     * Loads leave requests from the database.
     */
    private void loadLeaveRequests() {
        leaveRequests.clear();

        String query = "SELECT reason, time, date FROM leave_requests";
        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                leaveRequests.add(new LeaveRequest(
                        resultSet.getString("reason"),
                        resultSet.getString("time"),
                        resultSet.getString("date")
                ));
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to fetch leave requests: " + e.getMessage());
        }
    }

    /**
     * Handles accepting or rejecting a leave request.
     */
    private void handleDecision(int index, boolean isAccepted) {
        if (index < 0 || index >= leaveRequests.size()) {
            System.err.println("[ERROR] Invalid leave request index: " + index);
            return;
        }

        LeaveRequest request = leaveRequests.get(index);
        String decisionMessage = String.format(
                "[LEAVE REQUEST UPDATE] Reason: %s, Date: %s, Time: %s - Your request has been %s.",
                request.getReason(), request.getDate(), request.getTime(), isAccepted ? "ACCEPTED" : "REJECTED"
        );

        sendDecisionToServer(decisionMessage);
        leaveRequests.remove(index);
    }

    /**
     * Sends a decision message to the server.
     */
    private void sendDecisionToServer(String message) {
        if (serverWriter != null) {
            serverWriter.println(message);
            serverWriter.flush();
            System.out.println("[INFO] Decision sent to server: " + message);
        } else {
            System.err.println("[ERROR] Server connection is not established.");
        }
    }

    /**
     * LeaveRequest Model Class.
     */
    public static class LeaveRequest {
        private final String reason;
        private final String time;
        private final String date;

        public LeaveRequest(String reason, String time, String date) {
            this.reason = reason;
            this.time = time;
            this.date = date;
        }

        public String getReason() {
            return reason;
        }

        public String getTime() {
            return time;
        }

        public String getDate() {
            return date;
        }
    }

}
