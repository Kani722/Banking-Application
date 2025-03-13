module com.example.bankapplication {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;   // ✅ Required for MySQL connection
    requires java.base;  // ✅ Required by default (not needed explicitly unless minimized modules are used)
    requires java.desktop; // ✅ Required if using Swing components or AWT

    opens com.example.bankapplication to javafx.fxml;
    exports com.example.bankapplication;
}
