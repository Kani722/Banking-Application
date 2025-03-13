package com.example.bankapplication_admin;

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

public class LoginController implements Initializable {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Label errorLabel;

    @FXML
    private ImageView bankImageView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
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

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Validate credentials from database
        if (validateLogin(username, password)) {
            errorLabel.setText("Login successful!");
            loadDashboard();
        } else {
            errorLabel.setText("Invalid username or password.");
        }
    }

    private boolean validateLogin(String username, String password) {
        boolean isValid = false;

        // Get database connection
        DatabaseConnection dbConnection = new DatabaseConnection();
        Connection connection = dbConnection.connect();

        if (connection != null) {
            try {
                // Prepare SQL query to check the username and password
                String query = "SELECT * FROM adminUsers WHERE name = ? AND password = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);

                // Execute the query
                ResultSet resultSet = preparedStatement.executeQuery();

                // If a record exists, the login is valid
                if (resultSet.next()) {
                    isValid = true;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return isValid;
    }

    private void loadDashboard() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("dashboard-view.fxml"));
            Scene dashboardScene = new Scene(fxmlLoader.load(), 800, 500);

            Stage stage = (Stage) usernameField.getScene().getWindow(); // Get current stage
            stage.setScene(dashboardScene);
            stage.setTitle("Dashboard");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
