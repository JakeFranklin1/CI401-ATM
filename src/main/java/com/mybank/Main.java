package com.mybank;
import javafx.application.Platform;

public class Main {
    public static void main(String[] args) {
        Debug.trace("Main::Starting Program");
        Platform.setImplicitExit(true);
        App.main(args);
    }
}