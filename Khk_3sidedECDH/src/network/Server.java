package network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public abstract class Server extends Connection {

    protected ServerSocket serverSocket;
    protected ArrayList<ServerConnection> clientServerConnections;

    public Server(String id){
        super(id);
    }

    @Override
    protected void setup() throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.clientServerConnections = new ArrayList<>();
    }
    @Override
    protected void runCommunication() throws IOException {
        //System.out.println(this.id + ": waiting for connection...");
        Socket clientSocket = this.serverSocket.accept();
        ServerConnection serverConnection = getConnection(clientSocket);
        this.clientServerConnections.add(serverConnection);
        serverConnection.start();
        //System.out.println(this.id + ": New Client connected.");
    }

    protected abstract ServerConnection getConnection(Socket clientSocket);
}
