package com.example.bankapplication;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.HBox;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class ViewLoansController implements Initializable {

    @FXML
    private TableView<Loan> loanTableView;
    @FXML
    private TableColumn<Loan, Integer> loanIdColumn;
    @FXML
    private TableColumn<Loan, String> loanTypeColumn;
    @FXML
    private TableColumn<Loan, String> requesterColumn;
    @FXML
    private TableColumn<Loan, String> emailColumn;
    @FXML
    private TableColumn<Loan, String> contactColumn;
    @FXML
    private TableColumn<Loan, Double> loanPriceColumn;
    @FXML
    private TableColumn<Loan, Double> taxColumn;
    @FXML
    private TableColumn<Loan, Double> interestColumn;
    @FXML
    private TableColumn<Loan, HBox> acceptColumn;
    @FXML
    private TableColumn<Loan, HBox> sendToAdminColumn;
    @FXML
    private Button backButton;

    private static PrintWriter serverWriter;
    private BufferedReader serverReader;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        connectToServer();  // Establish server connection

        // Set up column mappings
        loanIdColumn.setCellValueFactory(new PropertyValueFactory<>("loanId"));
        loanTypeColumn.setCellValueFactory(new PropertyValueFactory<>("loanType"));
        requesterColumn.setCellValueFactory(new PropertyValueFactory<>("requesterName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        contactColumn.setCellValueFactory(new PropertyValueFactory<>("contactNumber"));
        loanPriceColumn.setCellValueFactory(new PropertyValueFactory<>("loanPrice"));
        taxColumn.setCellValueFactory(new PropertyValueFactory<>("tax"));
        interestColumn.setCellValueFactory(new PropertyValueFactory<>("interest"));

        // Setup accept and send to admin buttons
        acceptColumn.setCellValueFactory(param -> {
            Loan loan = param.getValue();
            Button acceptButton = new Button("Accept");
            acceptButton.setOnAction(event -> acceptLoan(loan));
            HBox hbox = new HBox(acceptButton);
            return new ReadOnlyObjectWrapper<>(hbox);
        });

        sendToAdminColumn.setCellValueFactory(param -> {
            Loan loan = param.getValue();
            Button sendToAdminButton = new Button("Send to Admin");
            sendToAdminButton.setOnAction(event -> sendLoanToAdmin(loan));
            HBox hbox = new HBox(sendToAdminButton);
            return new ReadOnlyObjectWrapper<>(hbox);
        });

        loadLoans();  // Loads loans from the database and populates the table
    }

    private void connectToServer() {
        try (Socket serverSocket = new Socket("192.168.8.172", 6000)) {  // Connect to local server
            serverWriter = new PrintWriter(serverSocket.getOutputStream(), true);
            serverReader = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
            System.out.println("[INFO] Connected to Loan Request Server.");
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to connect to server: " + e.getMessage());
        }
    }

    private static void sendMessageToServer(String message) {
        final String SERVER_IP = "192.168.8.172";  // Define the server IP
        final int SERVER_PORT = 6000;              // Define the server port

        System.out.println("Connecting to server at " + SERVER_IP + ":" + SERVER_PORT);

        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            out.println(message);
            showAlert("Success", "Loan data has been sent to the admin.", Alert.AlertType.INFORMATION);
            System.out.println("Loan data sent successfully: " + message);

        } catch (IOException e) {
            showAlert("Error", "Failed to send loan data to the admin.", Alert.AlertType.ERROR);
            System.err.println("Failed to send loan data. Error: " + e.getMessage());
        }
    }

    private static void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void sendLoanToAdmin(Loan loan) {
        String loanData = "Loan ID: " + loan.getLoanId() + ", Type: " + loan.getLoanType() +
                ", Requester: " + loan.getRequesterName() + ", Email: " + loan.getEmail() +
                ", Contact: " + loan.getContactNumber() + ", Price: " + loan.getLoanPrice() +
                ", Tax: " + loan.getTax() + ", Interest: " + loan.getInterest();

        sendMessageToServer(loanData);
    }

    @FXML
    private void handleBackButton() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("dashboard-view.fxml"));
            Scene dashboardScene = new Scene(fxmlLoader.load(), 800, 500);

            Stage stage = (Stage) backButton.getScene().getWindow(); // Get current stage
            stage.setScene(dashboardScene);
            stage.setTitle("Dashboard");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadLoans() {
        ObservableList<Loan> loans = FXCollections.observableArrayList();
        String query = "SELECT * FROM loans";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Loan loan = new Loan(rs.getInt("loan_id"), rs.getString("loan_type"),
                        rs.getString("requester_name"), rs.getString("email"),
                        rs.getString("contact_number"), rs.getDouble("loan_price"),
                        rs.getDouble("tax"), rs.getDouble("interest"));
                loans.add(loan);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        loanTableView.setItems(loans);
    }

    private void acceptLoan(Loan loan) {
        System.out.println("Loan ID " + loan.getLoanId() + " accepted.");
        // You can add additional logic to mark the loan as accepted or update its status in the database
    }
}
