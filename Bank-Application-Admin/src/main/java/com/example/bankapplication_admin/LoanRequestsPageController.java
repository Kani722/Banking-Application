package com.example.bankapplication_admin;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class LoanRequestsPageController {

    @FXML private TableView<LoanRequest> loanTable;
    @FXML private TableColumn<LoanRequest, Integer> loanIdCol;
    @FXML private TableColumn<LoanRequest, String> typeCol;
    @FXML private TableColumn<LoanRequest, String> requesterCol;
    @FXML private TableColumn<LoanRequest, String> emailCol;
    @FXML private TableColumn<LoanRequest, String> contactCol;
    @FXML private TableColumn<LoanRequest, Double> priceCol;
    @FXML private TableColumn<LoanRequest, Double> taxCol;
    @FXML private TableColumn<LoanRequest, Double> interestCol;
    @FXML
    private Button backButton;
    @FXML
    private TableColumn<LoanRequest, Void> actionColumn;
    // Initialize the TableView and load data from the database
    @FXML
    private void initialize() {
        loanIdCol.setCellValueFactory(new PropertyValueFactory<>("loanId"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        requesterCol.setCellValueFactory(new PropertyValueFactory<>("requester"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        contactCol.setCellValueFactory(new PropertyValueFactory<>("contact"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        taxCol.setCellValueFactory(new PropertyValueFactory<>("tax"));
        interestCol.setCellValueFactory(new PropertyValueFactory<>("interest"));

        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button acceptButton = new Button("Accept");
            private final Button rejectButton = new Button("Reject");
            private final HBox buttonsBox = new HBox(10, acceptButton, rejectButton);

            {
//                acceptButton.setOnAction(event -> handleDecision(getIndex(), true));
//                rejectButton.setOnAction(event -> handleDecision(getIndex(), false));
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

        loadLoanRequests();
    }

    // Load loan requests from the database
    private void loadLoanRequests() {
        ObservableList<LoanRequest> loanRequests = FXCollections.observableArrayList();

        try (Connection conn = DatabaseConnection.connect()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM loan_requests");

            while (rs.next()) {
                int loanId = rs.getInt("loan_id");
                String type = rs.getString("type");
                String requester = rs.getString("requester");
                String email = rs.getString("email");
                String contact = rs.getString("contact");
                double price = rs.getDouble("price");
                double tax = rs.getDouble("tax");
                double interest = rs.getDouble("interest");

                loanRequests.add(new LoanRequest(loanId, type, requester, email, contact, price, tax, interest));
            }

            loanTable.setItems(loanRequests);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleBackAction() {
        try {
            // Load the previous page (assuming it is "dashboard-view.fxml")
            FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard-view.fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 500)); // Adjust size if needed
            stage.setTitle("Dashboard");

            System.out.println("Navigated back to Dashboard");

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading the dashboard: " + e.getMessage());
        }
    }

//    private void handleDecision(int index, boolean isAccepted) {
//        if (index < 0 || index >= Requests.size()) {
//            System.err.println("[ERROR] Invalid leave request index: " + index);
//            return;
//        }
//
//        DashboardController.LeaveRequest request = leaveRequests.get(index);
//        String decisionMessage = String.format(
//                "[LEAVE REQUEST UPDATE] Reason: %s, Date: %s, Time: %s - Your request has been %s.",
//                request.getReason(), request.getDate(), request.getTime(), isAccepted ? "ACCEPTED" : "REJECTED"
//        );
//
//        sendDecisionToServer(decisionMessage);
//        leaveRequests.remove(index);
//    }


}