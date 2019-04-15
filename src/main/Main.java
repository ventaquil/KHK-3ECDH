package main;

import io.ArgsManager;
import io.InputManager;
import protocol.Parameters;
import protocol.network.User;
import protocol.network.Watchdog;

public class Main {
    public static void main(String[] args) {
        try {
            InputManager inputManager = new ArgsManager(args);
            Parameters parameters = inputManager.read();

            new Watchdog(parameters, inputManager).start();
            new User(parameters).start();
            new User(parameters).start();
            new User(parameters).start();

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
