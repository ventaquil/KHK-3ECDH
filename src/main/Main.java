package main;

import io.ArgsManager;
import io.InputManager;
import protocol.Parameters;
import protocol.network.User;
import protocol.network.Watchdog;

import java.math.BigInteger;

public class Main {
    public static void main(String[] args) {
        try {
            Parameters parameters = new ArgsManager(args).read();

            new Watchdog(parameters).start();
            new User(parameters).start();
            new User(parameters).start();
            new User(parameters).start();

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
