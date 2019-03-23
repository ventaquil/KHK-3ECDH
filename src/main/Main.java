package main;

import protocol.network.User;
import protocol.network.Watchdog;
import subprocess.Sage;

public class Main {
    public static void main(String[] args){
        try {
            System.out.println(new Sage().execute("import protocol; print(\"Hello\"); print(protocol.get_random_prime);"));
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        new Watchdog().start();
        new User().start();
        new User().start();
        new User().start();
    }
}
