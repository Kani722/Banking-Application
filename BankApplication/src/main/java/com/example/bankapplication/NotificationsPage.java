package com.example.bankapplication;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class NotificationsPage {

    @FXML
    private ListView<String> notificationsList;

    @FXML
    private Button backButton;

    @FXML
    public void initialize() {
        loadNotifications();
    }

    private void loadNotifications() {
        ObservableList<String> notifications = FXCollections.observableArrayList();

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT details, response, created_at FROM notifications ORDER BY created_at DESC")) {

            while (rs.next()) {
                String details = rs.getString("details");
                String response = rs.getString("response");
                String time = rs.getString("created_at");
                notifications.add(details + " - " + response + " (" + time + ")");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        notificationsList.setItems(notifications);
    }

    @FXML
    private void handleBackButton(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("dashboard-view.fxml"));
            Scene dashboardScene = new Scene(fxmlLoader.load(), 800, 500);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(dashboardScene);
            stage.setTitle("Dashboard");
            System.out.println("Navigating back to Dashboard.");

        } catch (IOException e) {
            System.err.println("Error loading Dashboard view: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
