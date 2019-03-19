package protocol.network;

import network.ServerConnection;
import protocol.gui.UserWindow;

import java.io.IOException;
import java.net.ServerSocket;

public class UserP2PServer extends ServerConnection {

     private int port;
     ServerSocket serverSocket;
     UserWindow window;

    public UserP2PServer(String id, int port, UserWindow window){
        super(id, null);
        this.port = port;
        this.window = window;
    }

    @Override
    protected void setup() throws IOException {
        this.serverSocket = new ServerSocket(this.port);
        this.clientSocket = this.serverSocket.accept();
        this.window.log("Client connected.");
        super.setup();
    }

    @Override
    protected void runCommunication() throws IOException {
        this.window.log(this.endpoint.receiveData());
    }
}
