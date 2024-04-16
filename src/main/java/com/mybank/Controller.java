package com.mybank;

import java.io.IOException;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class Controller {

    private Stage window;
    public View view;
    private Model model;

    public void setModel(Model model) {
        this.model = model;
    }

    public void setView(View view, Stage window) {
        this.view = view;
        this.window = window;
    }

    // FXML components

    @FXML
    private PasswordField currentPasswordField, newPasswordField, confirmPasswordField;
    @FXML
    private Button depositBtn, balanceBtn, statementBtn, withdrawBtn, transferBtn, changePassBtn;
    @FXML
    private TextField transactionAccNoField, message, changeOverdraft;
    @FXML
    private TextArea reply;

    public Controller() {
        Debug.trace("Controller::<constructor>");
        this.transactionAccNoField = new TextField();
        this.message = new TextField();
        this.reply = new TextArea();
    }

    public void update(String output1, String output2) {
        message.setText(output1);
        reply.setText(output2);
        // I added this so that the user cannot bug out the program by typing in the
        // text area during ATM operations.
        transactionAccNoField.addEventFilter(KeyEvent.KEY_TYPED, KeyEvent::consume);
        message.addEventFilter(KeyEvent.KEY_TYPED, KeyEvent::consume);
        reply.addEventFilter(KeyEvent.KEY_TYPED, KeyEvent::consume);
    }

    public void process(String action) {
        Debug.trace("Controller::process: action = " + action);
        switch (action) {
        case "1":
        case "2":
        case "3":
        case "4":
        case "5":
        case "6":
        case "7":
        case "8":
        case "9":
        case "0":
        case "00":
            System.out.println("Processing number: " + action);
            model.processNumber(action);
            break;
        case "ENTER":
            model.processEnter();
            break;
        case "CLEAR":
            model.processClear();
            break;
        case "LOGOUT":
            handleLogout(window, "login.fxml");
            break;
        case "CANCEL":
            model.processCancel();
            break;
        case "DEPOSIT":
            model.processDeposit();
            break;
        case "BALANCE":
            model.processBalance();
            break;
        case "STATEMENT":
            model.processStatement();
            break;
        case "WITHDRAW":
            model.processWithdraw();
            break;
        case "TRANSFER":
            model.processTransfer();
            break;
        case "ACCOUNT":
            showAccountManagementWindow();
            break;
        case "Change Password":
            handleChangePassword();
            break;
        case "Change Overdraft":
            handleOverdraftChange();
            break;
        default:
            model.processUnknownKey(action);
            break;
        }
    }

    @FXML
    public void handleButtonAction(ActionEvent event) {
        // Get the button that was clicked
        Button button = (Button) event.getSource();
        // Get the text of the button
        String buttonText = button.getText();
        process(buttonText);
    }

    public void showAccountManagementWindow() {
        try {
            String fxmlFile = "/com/mybank/account-without-overdraft.fxml";
            if (model.getCurrentAccount() instanceof OverdraftBankAccount) {
                fxmlFile = "/com/mybank/account-with-overdraft.fxml";
            }

            // Load the FXML file
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFile));
            fxmlLoader.setController(this);

            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
            Stage popupStage = new Stage();
            popupStage.setTitle("Account Management");
            popupStage.setScene(scene);
            popupStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleChangePassword() {
        // Get the text from the text fields
        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Change the password
        int result = model.changePassword(currentPassword, newPassword, confirmPassword);
        String alertText; // The text to display in the alert
        AlertType alertType; // The type of alert to display

        switch (result) {
        case 0:
            alertText = "Password changed successfully.";
            alertType = AlertType.INFORMATION;
            // Password change successful, clear the fields
            confirmPasswordField.clear();
            newPasswordField.clear();
            currentPasswordField.clear();
            break;
        case 1:
            alertText = "Current password is incorrect.";
            alertType = AlertType.ERROR;
            break;
        case 2:
            alertText = "New password and confirmed password do not match.";
            alertType = AlertType.ERROR;
            break;
        case 3:
            alertText = "New password is the same as the current password.";
            alertType = AlertType.ERROR;
            break;
        default:
            alertText = "Password change failed due to a general error, please try again.";
            alertType = AlertType.ERROR;
            break;
        }

        showAlert(alertType, "Password Change", alertText);
    }

    public void handleOverdraftChange() {
        String overdraft = changeOverdraft.getText();
        int result = model.changeOverdraft(overdraft);
        String alertText;
        AlertType alertType;

        switch (result) {
        case 0:
            alertText = "Overdraft changed successfully.";
            alertType = AlertType.INFORMATION;
            changeOverdraft.clear();
            break;
        case 1:
            alertText = "Overdraft must be a positive number.";
            alertType = AlertType.ERROR;
            break;
        case 2:
            alertText = "Overdraft must be greater than the current balance.";
            alertType = AlertType.ERROR;
            break;
        case 3:
            alertText = "This account does not have an overdraft facility.";
            alertType = AlertType.ERROR;
            break;
        default:
            alertText = "Overdraft change failed due to a general error, try again later.";
            alertType = AlertType.ERROR;
            break;
        }

        showAlert(alertType, "Overdraft Change", alertText);
    }

    public void showAlert(AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null); // Header is not used in this version of the method
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void handleLogout(Stage window, String fxmlFile) {
        model.processLogout();
        view.start(window, fxmlFile);
    }
}