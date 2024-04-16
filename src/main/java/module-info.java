module com.mybank {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;

    opens com.mybank to javafx.fxml;
    exports com.mybank;
}
