package network;

import java.io.IOException;
import java.net.Socket;

public abstract class ServerConnection extends Connection {

    protected Socket clientSocket;

    public ServerConnection(String id, Socket clientSocket){
        super(id);
        this.clientSocket = clientSocket;
    }

    @Override
    protected void setup() throws IOException {
        this.endpoint.setDataInput(this.clientSocket.getInputStream());
        this.endpoint.setDataOutput(this.clientSocket.getOutputStream());
    }
}
