package com.example.bankapplication;

import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class Loan {
    private int loanId;
    private String loanType;
    private String requesterName;
    private String email;
    private String contactNumber;
    private double loanPrice;
    private double tax;
    private double interest;

    // Buttons for each loan
    private Button acceptButton;
    private Button sendToAdminButton;

    public Loan(int loanId, String loanType, String requesterName, String email, String contactNumber,
                double loanPrice, double tax, double interest) {
        this.loanId = loanId;
        this.loanType = loanType;
        this.requesterName = requesterName;
        this.email = email;
        this.contactNumber = contactNumber;
        this.loanPrice = loanPrice;
        this.tax = tax;
        this.interest = interest;

        // Create buttons for accept and send to admin
        this.acceptButton = new Button("Accept");
        this.sendToAdminButton = new Button("Send to Admin");

        // Handle button actions
        this.acceptButton.setOnAction(event -> handleAcceptLoan());
        this.sendToAdminButton.setOnAction(event -> handleSendToAdmin());
    }

    // Getter methods for all fields
    public int getLoanId() {
        return loanId;
    }

    public String getLoanType() {
        return loanType;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public String getEmail() {
        return email;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public double getLoanPrice() {
        return loanPrice;
    }

    public double getTax() {
        return tax;
    }

    public double getInterest() {
        return interest;
    }

    public Button getAcceptButton() {
        return acceptButton;
    }

    public Button getSendToAdminButton() {
        return sendToAdminButton;
    }

    // Accept Loan Action (you can add logic here, like updating database or status)
    private void handleAcceptLoan() {
        System.out.println("Loan ID " + loanId + " accepted.");
        // Update database with loan acceptance, and disable button or change text
        acceptButton.setText("Accepted");
        acceptButton.setDisable(true); // Disables the button after acceptance
    }

    // Send Loan to Admin Action
    private void handleSendToAdmin() {
        System.out.println("Loan ID " + loanId + " sent to admin.");
        // You could add communication with the server here (e.g., send message to server)
        sendLoanToAdmin();
    }

    private void sendLoanToAdmin() {
        String loanData = "Loan ID: " + loanId + ", Type: " + loanType +
                ", Requester: " + requesterName + ", Email: " + email +
                ", Contact: " + contactNumber + ", Price: " + loanPrice +
                ", Tax: " + tax + ", Interest: " + interest;

        // Add logic to send this data to the server (could be in a separate method)
        System.out.println("Sending loan data to admin: " + loanData);
        // Example: sendMessageToServer(loanData); from ViewLoansController
    }
}
