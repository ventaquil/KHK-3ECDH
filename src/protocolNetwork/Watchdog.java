package protocolNetwork;

import network.Server;
import network.ServerConnection;
import protocolGUI.WatchdogWindow;

import java.io.IOException;
import java.net.Socket;

public class Watchdog extends Server {

    private WatchdogWindow window;

    public Watchdog(){
        super("Watchdog");
        this.window = new WatchdogWindow(this, this.id);
        new Thread(this.window).start();
    }

    @Override
    protected void runCommunication() throws IOException {
        super.runCommunication();
        this.window.log("Client connected.");
    }

    @Override
    protected ServerConnection getConnection(Socket clientSocket) {
        return new WatchdogConnection("Watchdog", clientSocket, this.window);
    }
}
