module com.shreeniketh.hotelmanagement {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires java.sql;

    exports com.shreeniketh.hotelmanagement;
    exports com.shreeniketh.hotelmanagement.model;

    opens com.shreeniketh.hotelmanagement.controller to javafx.fxml;
}