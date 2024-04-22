package CI401.mybank;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The View class represents the user interface of the ATM application.
 * It is responsible for displaying the graphical user interface and handling user interactions.
 * The View class uses the Model class to access and update the data, and the Controller class
 * to handle user input and update the view accordingly.
 * The View class is part of the model-view-controller (MVC) design pattern.
 */
public class View {
    // The other parts of the model-view-controller setup
    public Model model;
    public Controller controller;
    public LoginController loginController; // Add this line

    /**
     * Sets the controller for this view.
     *
     * @param controller the controller to set
     */
    public void setController(Controller controller) {
        this.controller = controller;
    }

    /**
     * Constructs a new View object and initializes the login controller.
     */
    public View() {
        Debug.trace("View::<constructor>");
        loginController = new LoginController(); // Add this line
    }

    /**
     * Starts the view by loading the specified FXML file, setting the login controller as the controller,
     * and showing the scene in the specified window.
     *
     * @param window the window in which to show the scene
     * @param fxmlFile the FXML file to load
     */
    public void start(Stage window, String fxmlFile) {
        Debug.trace("View::start");

        try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CI401/mybank/" + fxmlFile));

            // Set this class as the controller
            loader.setController(loginController);

            // Load the scene, and center it.
            Scene scene = new Scene(loader.load());
            window.setScene(scene);
            window.centerOnScreen();
            window.setTitle("ATM Login");
            window.show();
            window.setResizable(false);

        } catch (IOException e) {
            Debug.trace("Failed to load the FXML file.");
            e.printStackTrace();
        }
    }
}