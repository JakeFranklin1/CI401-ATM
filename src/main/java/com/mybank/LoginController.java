package com.mybank;

import java.net.URL;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class LoginController {
    Controller controller = new Controller();

    @FXML
    private TextField accountField;

    @FXML
    private Button loginButton;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleLoginAction(ActionEvent event) {
        // Get the account number and password from the text fields
        String accountNumber = accountField.getText();
        String password = passwordField.getText();
        // Create a Bank object
        Bank b = new Bank();
        if (b.login(Integer.parseInt(accountNumber), password)) {
            try {
                URL url = getClass().getResource("/com/mybank/atm.fxml");
                if (url == null) {
                    System.err.println("Unable to load 'atm.fxml'.");
                    return;
                }

                FXMLLoader loader = new FXMLLoader(url);
                loader.setController(controller); // Set the controller
                // Load FXML scene
                Parent root = loader.load();
                Scene scene = new Scene(root);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.centerOnScreen();
                stage.setTitle("ATM");
                stage.show();

                Model model = new Model(b); // the model needs the Bank object to 'talk to' the bank
                View view = new View();
                // Set the model and view for the controller
                controller.setModel(model);
                controller.setView(view, stage);
                // Set the controller for the model and view
                model.setController(controller);
                view.setController(controller);
                // Set the state of the model and the account.
                model.setAccount(Integer.parseInt(accountNumber), password);
                controller.update("Welcome to the ATM", "");

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Debug.trace("LoginController::handleLoginAction:: Login failed for account number: " + accountNumber);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Error");
            alert.setHeaderText("Login failed");
            alert.setContentText("Invalid account number or password.");
            // Waits for the user to close the alert before continuing the program
            alert.showAndWait();
        }
    }
}