/**
 * The com.mybank module contains classes and resources related to the banking
 * functionality of the application.
 * It requires the JavaFX controls module, JavaFX FXML module, JavaFX graphics
 * module, and the Bouncy Castle provider module.
 * The module is opened to JavaFX FXML for loading FXML files, and it exports
 * the com.mybank package for other modules to use.
 */
module com.mybank {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires org.bouncycastle.provider;

    opens com.mybank to javafx.fxml;

    exports com.mybank;
}
