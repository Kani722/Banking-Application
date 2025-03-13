package com.example.bankapplication;

import java.io.Serializable;

public class LeaveRequest implements Serializable {
    private String reason;
    private String time;
    private String date;

    public LeaveRequest(String reason, String time, String date) {
        this.reason = reason;
        this.time = time;
        this.date = date;
    }

    public String getReason() { return reason; }
    public String getTime() { return time; }
    public String getDate() { return date; }

    @Override
    public String toString() {
        return "LeaveRequest{reason='" + reason + "', time='" + time + "', date='" + date + "'}";
    }
}
