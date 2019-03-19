package main;

import protocol.network.User;
import protocol.network.Watchdog;

public class Main {
    public static void main(String[] args){
        new Watchdog().start();
        new User().start();
        new User().start();
        new User().start();
    }
}
