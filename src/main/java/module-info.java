module com.mybank {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;

    opens com.mybank to javafx.fxml;
    exports com.mybank;
}
