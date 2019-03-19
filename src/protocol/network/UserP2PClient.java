package protocol.network;

import network.Connection;
import protocol.gui.UserWindow;

import java.io.IOException;
import java.net.Socket;

public class UserP2PClient extends Connection {

    private int port;
    private Socket clientSocket;
    private UserWindow window;

    public UserP2PClient(String id, int port, UserWindow window){
        super(id);
        this.port = port;
        this.window = window;
    }

    @Override
    protected void setup() throws IOException {
        this.clientSocket = new Socket(ip, this.port);
        this.endpoint.setDataInput(this.clientSocket.getInputStream());
        this.endpoint.setDataOutput(this.clientSocket.getOutputStream());
        this.window.log("Logged in.");
    }

    @Override
    protected void runCommunication() throws IOException {
        this.window.log(this.endpoint.receiveData());
    }
}
