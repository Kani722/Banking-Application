package com.example.bankapplication_admin;

import javafx.beans.property.*;

public class LoanRequest {
    private final int loanId;
    private final String type;
    private final String requester;
    private final String email;
    private final String contact;
    private final double price;
    private final double tax;
    private final double interest;

    public LoanRequest(int loanId, String type, String requester, String email, String contact, double price, double tax, double interest) {
        this.loanId = loanId;
        this.type = type;
        this.requester = requester;
        this.email = email;
        this.contact = contact;
        this.price = price;
        this.tax = tax;
        this.interest = interest;
    }

    // Getters for each field
    public int getLoanId() { return loanId; }
    public String getType() { return type; }
    public String getRequester() { return requester; }
    public String getEmail() { return email; }
    public String getContact() { return contact; }
    public double getPrice() { return price; }
    public double getTax() { return tax; }
    public double getInterest() { return interest; }
}

