package com.example.bankapplication_admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LeaveRequestTable {
    private TableView<LeaveRequest> table;

    public LeaveRequestTable() {
        table = new TableView<>();

        // Create columns
        TableColumn<LeaveRequest, String> reasonCol = new TableColumn<>("Reason");
        reasonCol.setCellValueFactory(new PropertyValueFactory<>("reason"));

        TableColumn<LeaveRequest, String> timeCol = new TableColumn<>("Time");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));

        TableColumn<LeaveRequest, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

        table.getColumns().addAll(reasonCol, timeCol, dateCol);

        loadLeaveRequests(); // Load data from the database
    }

    public void loadLeaveRequests() {
        ObservableList<LeaveRequest> leaveRequests = FXCollections.observableArrayList();
        String query = "SELECT reason, time, date FROM leave_requests";

        try (Connection connection = DatabaseConnection.connect();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                leaveRequests.add(new LeaveRequest(
                        resultSet.getString("reason"),
                        resultSet.getString("time"),
                        resultSet.getString("date")
                ));
            }

            table.setItems(leaveRequests);

        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to fetch leave requests: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public TableView<LeaveRequest> getTable() {
        return table;
    }
}
