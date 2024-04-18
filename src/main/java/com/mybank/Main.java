package com.mybank;

import javafx.application.Platform;

/**
 * The Main class is the entry point of the ATM program.
 * It contains the main method that starts the program.
 * I was having some difficulties getting Maven to build the project
 * This was because it doesn't seem to like the main class to be extending anything
 * Which in this case would be Application (from JavaFX)
 * The solution was to make a new Main class which starts the old Main class (App)
 */

public class Main {
    public static void main(String[] args) {
        Debug.trace("Main::Starting Program");
        // This is to make sure the program closes when the last window is closed
        // otherwise it will keep running in the background and produce an error.
        Platform.setImplicitExit(true);
        App.main(args);
    }
}
