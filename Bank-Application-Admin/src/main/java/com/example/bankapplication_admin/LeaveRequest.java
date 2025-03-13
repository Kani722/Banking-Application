package com.example.bankapplication_admin;

import javafx.beans.property.SimpleStringProperty;

public class LeaveRequest {
    private final SimpleStringProperty reason;
    private final SimpleStringProperty time;
    private final SimpleStringProperty date;

    public LeaveRequest(String reason, String time, String date) {
        this.reason = new SimpleStringProperty(reason);
        this.time = new SimpleStringProperty(time);
        this.date = new SimpleStringProperty(date);
    }

    public String getReason() { return reason.get(); }
    public String getTime() { return time.get(); }
    public String getDate() { return date.get(); }
}
