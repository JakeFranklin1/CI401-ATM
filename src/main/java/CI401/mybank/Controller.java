package CI401.mybank;

import java.io.IOException;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
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
import javafx.scene.control.Label;

/**
 * Controller class for the bank application.
 * Handles user interactions and updates the view and model accordingly.
 * The Controller class is part of the model-view-controller
 * (MVC) design pattern.
 * It is responsible for processing user input, updating the model,
 * and updating the view.
 */
public class Controller {

    public View view;
    private Model model;
    private Stage window;

    /**
     * Sets the model for this controller.
     * 
     * @param model The model to be set.
     */
    public void setModel(Model model) {
        this.model = model;
    }

    /**
     * Sets the view and window for this controller.
     * 
     * @param view   The view to be set.
     * @param window The window to be set.
     */
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
    @FXML
    private Label timeLabel;
    @FXML
    private Label dateLabel;

    /**
     * Constructor for the Controller class.
     * Initializes the text fields and text area.
     */
    public Controller() {
        Debug.trace("Controller::<constructor>");
        this.transactionAccNoField = new TextField();
        this.message = new TextField();
        this.reply = new TextArea();
    }

    /**
     * Initializes the controller by setting up a recurring task to update the date
     * and time every minute.
     * This is done using the updateDateTime method and a JavaFX Timeline for
     * scheduling.
     */
    public void initialise() {
        // Create a new Timeline that triggers every second, making sure the clock
        // is always correct.
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> updateDateTime()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        updateDateTime();
    }

    /**
     * Updates the date and time labels with the current date and time.
     */
    private void updateDateTime() {
        String[] dateTime = DateTimeUtils.getCurrentDateTime();

        dateLabel.setText(dateTime[0]);
        timeLabel.setText(dateTime[1]);
    }

    /**
     * Updates the message and reply text fields.
     * Also disables the text fields to prevent user input during ATM operations.
     * 
     * @param output1 The text to be set in the message text field.
     * @param output2 The text to be set in the reply text area.
     */
    public void update(String output1, String output2) {
        message.setText(output1);
        reply.setText(output2);
        // I added this so that the user cannot bug out the program by typing in the
        // text area during ATM operations.
        transactionAccNoField.addEventFilter(KeyEvent.KEY_TYPED, KeyEvent::consume);
        message.addEventFilter(KeyEvent.KEY_TYPED, KeyEvent::consume);
        reply.addEventFilter(KeyEvent.KEY_TYPED, KeyEvent::consume);
    }

    /**
     * Processes the given action.
     * Actions include number inputs, enter, clear, logout, cancel,
     * deposit, balance, statement, withdraw, transfer,
     * account, Change Password, and Change Overdraft.
     * 
     * @param action The action to be processed.
     */
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

    /**
     * Handles button actions.
     * 
     * @param event The action event to be handled.
     */
    @FXML
    public void handleButtonAction(ActionEvent event) {
        // Get the button that was clicked
        Button button = (Button) event.getSource();
        // Get the text of the button
        String buttonText = button.getText();
        process(buttonText);
    }

    /**
     * Shows the account management window.
     * The window displays different options for regular accounts, and overdraft
     * accounts.
     */
    public void showAccountManagementWindow() {
        try {
            String fxmlFile = "/CI401/mybank/account-without-overdraft.fxml";
            if (model.getCurrentAccount() instanceof OverdraftBankAccount) {
                fxmlFile = "/CI401/mybank/account-with-overdraft.fxml";
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

    /**
     * Handles password changes.
     * Checks if the current password is correct, if the new password and confirmed
     * password match, and if the new password is different from
     * the current password.
     * Displays an alert based on the result.
     */
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

    /**
     * Handles overdraft changes.
     * Checks if the new overdraft is a positive number, greater than the current
     * balance, and if the account has an overdraft facility.
     * Displays an alert based on the result.
     */
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
            case 4:
                alertText = "Overdraft cannot be above 1,000.";
                alertType = AlertType.ERROR;
                break;
            default:
                alertText = "Overdraft change failed due to a general error, try again later.";
                alertType = AlertType.ERROR;
                break;
        }

        showAlert(alertType, "Overdraft Change", alertText);
    }

    /**
     * Shows an alert with the given parameters.
     * 
     * @param alertType The type of the alert.
     * @param title     The title of the alert.
     * @param content   The content of the alert.
     */
    public void showAlert(AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);

        alert.setTitle(title);
        alert.setHeaderText(null); // Header is not used in this version of the method
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Handles logout.
     * Logs out the user and loads the login scene by calling the start
     * Method in View.
     * 
     * @param window   The window to be logged out from.
     * @param fxmlFile The FXML file to be loaded after logout.
     */
    public void handleLogout(Stage window, String fxmlFile) {
        model.processLogout();
        view.start(window, fxmlFile);
    }
}