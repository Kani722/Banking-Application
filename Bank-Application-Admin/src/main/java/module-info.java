module com.example.bankapplication {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop; // ✅ Required for MySQL connection

    opens com.example.bankapplication_admin to javafx.fxml;
    exports com.example.bankapplication_admin;
}
