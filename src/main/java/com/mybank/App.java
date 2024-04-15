package com.mybank;

import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {
    public static void main(String args[]) {
        launch(args);
    }

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