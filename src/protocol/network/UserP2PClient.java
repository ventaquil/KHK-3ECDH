package protocol.network;

import network.Connection;
import protocol.gui.UserWindow;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Semaphore;

public class UserP2PClient extends Connection {

    private int port;
    private User user;
    private Socket clientSocket;
    private UserWindow window;
    private Semaphore semaphore, comSem;

    public UserP2PClient(User user, String id, int port, UserWindow window, Semaphore semaphore){
        super(id);
        this.user = user;
        this.port = port;
        this.window = window;
        this.semaphore = semaphore;
        this.comSem = new Semaphore(1);
    }

    @Override
    protected void setup() throws IOException {
        try {
            this.comSem.acquire();
        } catch (InterruptedException e) {}
        this.clientSocket = new Socket(ip, this.port);
        this.endpoint.setDataInput(this.clientSocket.getInputStream());
        this.endpoint.setDataOutput(this.clientSocket.getOutputStream());
        this.semaphore.release();
    }

    @Override
    protected void runCommunication() throws IOException {
        try {
            this.comSem.acquire();
        } catch (InterruptedException e) {}
        this.window.log(this.endpoint.receiveData());
        this.comSem.release();
    }

    public void enableCommunication(){
        this.comSem.release();
    }
}
