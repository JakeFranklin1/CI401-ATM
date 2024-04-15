package com.mybank;
// The View class creates and manages the GUI for the application.

// It doesn't know anything about the ATM itself, it just displays
// the current state of the Model, (title, output1 and output2), 
// and handles user input from the buttons and handles user input

// We import lots of JavaFX libraries (we may not use them all, but it
// saves us having to thinkabout them if we add new code)
import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class View {
    // The other parts of the model-view-controller setup
    public Model model;
    public Controller controller;
    public LoginController loginController; // Add this line

    public void setController(Controller controller) {
        this.controller = controller;
    }

    // we don't really need a constructor method, but include one to print a
    // debugging message if required
    public View() {
        Debug.trace("View::<constructor>");
        loginController = new LoginController(); // Add this line
    }

    public void start(Stage window, String fxmlFile) {
        Debug.trace("View::start");

        try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mybank/" + fxmlFile));

            // Set this class as the controller
            loader.setController(loginController);

            // Load the scene, and center it.
            Scene scene = new Scene(loader.load());
            window.setScene(scene);
            window.centerOnScreen();
            window.setTitle("ATM Login");
            window.show();

        } catch (IOException e) {
            Debug.trace("Failed to load the FXML file.");
            e.printStackTrace();
        }
    }
}
