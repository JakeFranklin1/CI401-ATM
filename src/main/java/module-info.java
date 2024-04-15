module com.mybank {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.mybank to javafx.fxml;
    exports com.mybank;
}
