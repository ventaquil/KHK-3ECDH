package main;

import protocol.Parameters;
import protocol.network.User;
import protocol.network.Watchdog;

public class Main {
    public static void main(String[] args){
        try {
            Parameters parameters = new Parameters(4);

            System.out.println("Prime p: " + parameters.getP());
            System.out.println("Secret key: " + parameters.getSecretKey());
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        new Watchdog().start();
        new User().start();
        new User().start();
        new User().start();
    }
}
