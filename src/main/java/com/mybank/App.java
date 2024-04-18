package com.mybank;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * The main class of the ATM application.
 * This class extends the JavaFX Application class.
 * It contains the main method that starts the program
 * by calling the start method of the View class.
 * The start method loads the login.fxml file and
 * displays the login screen to the user.
 */
public class App extends Application {
    /**
     * The main method that is the entry point for the application.
     * It calls the launch method which in turn calls the start method.
     *
     * @param args Command line arguments.
     */
    public static void main(String args[]) {
        launch(args);
    }

    /**
     * The start method that is called by the launch method.
     * It sets up debugging, creates a new View, and starts the view.
     *
     * @param window The primary stage for this application, onto which
     *               the application scene can be set. 
     */
    public void start(Stage window) {
        // set up debugging and print initial debugging message
        Debug.set(true);
        Debug.trace("atm starting");
        Debug.trace("App::start");

        View view = new View();
        view.start(window, "login.fxml"); // load login.fxml instead of atm.fxml

        // application is now running
        Debug.trace("atm running");
    }
}