package com.example.bankapplication;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import java.util.ResourceBundle;
import javafx.scene.layout.AnchorPane;
import java.io.IOException;
import java.net.URL;
import java.sql.*;

public class DashboardController implements Initializable {



    @FXML
    private ListView<String> loanListView; // ListView to display loan details

//    @FXML
//    private Button notificationsButton;

    @FXML
    private ImageView bankImageView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            // Load image from "resources/images/" folder
            URL imageUrl = getClass().getResource("/images/login page image.png");

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
    // This method is called when the "View Loans" button is clicked

    // Add this in your FXML file


    // Method to handle Notifications button click
    @FXML
    public void handleNotificationsButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("notifications.fxml"));
            Scene scene = new Scene(loader.load(), 800, 500);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Notifications");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void handleViewLoansButton(ActionEvent event) {
        try {
            // Load the ViewLoans page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("view_loans.fxml"));

            // Make sure the FXML file is properly loaded
            Parent root = loader.load();

            // Create a new Scene with the loaded FXML
            Scene scene = new Scene(root, 800, 500);

            // Get the current stage and set the new scene
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("View Loans");
            stage.show();  // Ensure the stage is visible after setting the scene
        } catch (IOException e) {
            e.printStackTrace();
            // You can add a more descriptive error message

        }
    }







    @FXML
    public void handleAskForLeave(ActionEvent event) {
        try {
            // Load the AskLeave page (ask-leave.fxml)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ask-leave.fxml"));
            AnchorPane askLeavePage = loader.load(); // Load the fxml into the AnchorPane

            // Get the controller for the AskLeave page
            AskLeave askLeaveController = loader.getController();
            // If you need to pass data, do it here: askLeaveController.setSomeData(data);

            // Create a new scene for the AskLeave page
            Scene scene = new Scene(askLeavePage, 800, 500);

            // Get the current stage and set the new scene
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Ask for Leave");

        } catch (IOException e) {
            e.printStackTrace();  // Handle error loading FXML
        }
    }
}