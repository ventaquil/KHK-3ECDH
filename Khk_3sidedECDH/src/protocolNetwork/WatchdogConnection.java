package protocolNetwork;

import network.ServerConnection;
import protocolGUI.WatchdogWindow;

import java.io.IOException;
import java.net.Socket;

public class WatchdogConnection extends ServerConnection {

    private WatchdogWindow window;

    public WatchdogConnection(String id, Socket clientSocket, WatchdogWindow window) {
        super(id, clientSocket);
        this.window = window;
    }

    @Override
    protected void runCommunication() throws IOException {
        String message = this.endpoint.receiveData();
        this.window.log(message);
    }
}