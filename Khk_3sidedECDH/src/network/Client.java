package network;

import java.io.*;
import java.net.Socket;

public abstract class Client extends Connection {

    private static int no = 0;
    protected int uid;
    protected Socket clientSocket;

    public Client(){
        super("Client_" + no);
        this.uid = no++;
    }

    @Override
    protected void setup() throws IOException {
        this.clientSocket = new Socket(ip, port);
        this.endpoint.setDataInput(clientSocket.getInputStream());
        this.endpoint.setDataOutput(clientSocket.getOutputStream());
    }
}
