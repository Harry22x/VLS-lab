module com.example.hellofx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.rmi;
    requires java.sql;

    exports com.example.hellofx.rmi;

    opens com.example.hellofx to javafx.fxml;
    exports com.example.hellofx;
}