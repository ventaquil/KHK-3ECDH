package main;

import protocolNetwork.User;
import protocolNetwork.Watchdog;

public class Main {
    public static void main(String[] args){
        new Watchdog().start();
        new User().start();
        new User().start();
        new User().start();
    }
}
